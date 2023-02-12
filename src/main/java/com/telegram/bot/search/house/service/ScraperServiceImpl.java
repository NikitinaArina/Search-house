package com.telegram.bot.search.house.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.telegram.bot.search.house.dto.Month;
import com.telegram.bot.search.house.dto.OwnerDto;
import com.telegram.bot.search.house.dto.RenovationDto;
import com.telegram.bot.search.house.dto.ResponseDto;
import com.telegram.bot.search.house.repository.AdRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.telegram.bot.search.house.constants.Constants.*;

@Service
public class ScraperServiceImpl {
    private static final Logger LOGGER = LogManager.getLogger(ScraperServiceImpl.class);
    @Value("${urls}")
    private String[] url;
    private final AdRepository adRepository;

    @Autowired
    public ScraperServiceImpl(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    public void getAds() {
        //extractDataFromCian(url[0]);
        extractDataFromAvito(url[1]);
    }

    private void extractDataFromCian(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent(USER_AGENT));
            Page page = browserContext
                    .newPage();
            Page.NavigateOptions options = new Page.NavigateOptions()
                    .setTimeout(30000)
                    .setWaitUntil(WaitUntilState.LOAD);
            page.navigate(url, options);

            List<ElementHandle> ads = page.locator("article").elementHandles();

            for (ElementHandle ad : ads) {
                ResponseDto responseDTO = new ResponseDto();

                String apartLink = ad.querySelector("//div[@data-name='LinkArea']/a[@href]").getProperty("href").toString();
                LOGGER.info(apartLink);
                responseDTO.setUrl(apartLink);

                BrowserContext browserContext2 = browser.newContext(new Browser.NewContextOptions()
                        .setUserAgent(USER_AGENT));
                Page newPage = browserContext2.newPage();

                newPage.navigate(apartLink);

                String selectorMain = "//main";
                ElementHandle main;

                Locator locator = newPage.locator(selectorMain);

                while (!locator.isVisible()) {
                    LOGGER.info("Selector main not visible. Page and browser context recreated");
                    newPage.close();
                    browserContext2.close();
                    browserContext2 = browser.newContext(new Browser.NewContextOptions()
                            .setUserAgent(USER_AGENT));
                    newPage = browserContext2.newPage();
                    try {
                        newPage.navigate(apartLink);
                    } catch (TimeoutError ignored) {

                    }
                    locator = newPage.locator(selectorMain);
                }
                main = locator.elementHandle();

                ElementHandle title = main.querySelector("//div[@data-name='OfferTitle']");

                responseDTO.setTitle(title.innerText());
                responseDTO.setLocation(main.querySelector("//div[@data-name='Geo']").innerText().replace("На карте", ""));
                responseDTO.setPrice(Long.valueOf(main.querySelector("//span[@itemprop='price']").innerText()
                        .replaceFirst(EMPTY_REGEX, "")
                        .split(EMPTY_REGEX)[0]));
                responseDTO.setSquare(Long.valueOf(main.querySelectorAll("//div[@data-testid='object-summary-description-value']").get(0).innerText()
                        .split(EMPTY_REGEX)[0]
                        .split(COMMA)[0]));
                responseDTO.setFloor(Long.valueOf(main.querySelectorAll("//div[@data-testid='object-summary-description-info']").stream()
                        .filter(f -> f.innerHTML().contains(FLOOR))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(UNKNOWN)
                        .split(SPACE_REGEX)[0]));

                String rooms = title.innerText().split(COMMA)[0]
                        .split("-")[0];
                responseDTO.setRooms(rooms.contains("Студия") ? 0 : Long.parseLong(rooms));

                String year = main.querySelectorAll("//div[@data-testid='object-summary-description-info']").stream()
                        .filter(f -> f.innerHTML().contains("Построен"))
                        .map(m -> m.querySelector("//div[contains(@class, 'value')]"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(ZERO);
                responseDTO.setYear(Long.valueOf(year));

                String date = main.querySelector("//div[@data-name='OfferMeta']//div[contains(@data-name, 'OfferAdded')]").innerText();

                String[] time = date.split(SPACE_REGEX);
                String[] splittedTime = time.length > 2 ? time[2].split(COLON) : time[1].split(COLON);
                int hours = Integer.parseInt(splittedTime[0]);
                int minutes = Integer.parseInt(splittedTime[1]);

                setDate(responseDTO, date, time, hours, minutes, true);

                ElementHandle owner = main.querySelector("//div[@data-name='HomeownerBlockAside']/div[contains(@class, 'title')]");

                if (owner != null) {
                    responseDTO.setOwner(OwnerDto.OWNER);
                } else
                    responseDTO.setOwner(OwnerDto.AGENT);

                ElementHandle kids = main.querySelector("//ul[@data-name='Tenants']/li[contains(@class, 'kids')]");
                if (kids != null) {
                    responseDTO.setKids(kids.innerText().contains(ALLOWED));
                } else responseDTO.setKids(false);

                ElementHandle pets = main.querySelector("//ul[@data-name='Tenants']/li[contains(@class, 'pets')]");
                if (pets != null) {
                    responseDTO.setKids(pets.innerText().contains(ALLOWED));
                } else responseDTO.setAnimal(false);

                String renovation = main.querySelectorAll("//article[@data-name='AdditionalFeaturesGroup']//li").stream()
                        .filter(f -> f.innerHTML().contains(RENOVATION))
                        .map(m -> m.querySelector("//span[contains(@class, 'value')]"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(UNKNOWN);

                responseDTO.setRenovationType(RenovationDto.getByRenovation(renovation));

                if (!isAdContainsInDB(responseDTO)) adRepository.save(responseDTO.getAd());

                newPage.close();
            }
            browser.close();
        }
    }

    private void extractDataFromAvito(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Browser.NewContextOptions browserContextOptions = new Browser.NewContextOptions()
                    .setUserAgent(USER_AGENT);
            BrowserContext browserContext = browser.newContext(browserContextOptions);
            Page page = browserContext
                    .newPage();
            Page.NavigateOptions options = new Page.NavigateOptions()
                    .setTimeout(30000)
                    .setWaitUntil(WaitUntilState.COMMIT);
            page.navigate(url, options);

            List<ElementHandle> ads = page.querySelectorAll("//div[@data-marker='item']");

            for (ElementHandle ad : ads) {
                ResponseDto responseDTO = new ResponseDto();

                String apartLink = "https://www.avito.ru" + ad.querySelector("//a[@itemprop='url']").getAttribute("href");
                responseDTO.setUrl(apartLink);

                Page newPage = browserContext.newPage();

                newPage.navigate(apartLink, options);
                synchronized (newPage) {
                    newPage.wait(10000);
                }

                String selectorMain = "//div[contains(@class, 'style-item-view-page')]";
                ElementHandle main = newPage.querySelector(selectorMain);

                LOGGER.info(apartLink);

                List<ElementHandle> prices = main.querySelectorAll("//span[contains(@class, 'style-price-value-main')]");
                String price = prices.get(1)
                        .innerText()
                        .split(NEW_LINE)[0]
                        .replace(EMPTY_REGEX, "");
                responseDTO.setPrice(price.contains("₽") ? Long.valueOf(price.substring(0, price.indexOf("₽"))) : Long.valueOf(price));
                responseDTO.setLocation(main.querySelector("//div[@itemprop='address']").innerText().replaceAll(NEW_LINE, " "));
                responseDTO.setTitle(main.querySelector("//div[contains(@class, 'title-info-main')]").innerText());

                List<ElementHandle> infoList = main.querySelectorAll("//li[contains(@class, 'paramsList__item')]");

                String rooms = infoList.stream()
                        .filter(f -> f.innerHTML().contains("Количество комнат"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(ZERO)
                        .split(SPACE_REGEX)[2];

                responseDTO.setRooms(rooms.contains("студия") ? 0 : Long.parseLong(rooms));

                responseDTO.setRenovationType(RenovationDto.getByRenovation(infoList.stream()
                        .filter(f -> f.innerHTML().contains(RENOVATION))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(UNKNOWN)
                        .split(SPACE_REGEX)[1]));

                responseDTO.setSquare(Long.valueOf(infoList.stream()
                        .filter(f -> f.innerHTML().contains("Общая площадь"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(ZERO)
                        .split(SPACE_REGEX)[2]
                        .split(EMPTY_REGEX)[0]
                        .split("\\.")[0]));

                responseDTO.setFloor(Long.valueOf(infoList.stream()
                        .filter(f -> f.innerHTML().contains(FLOOR))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(ZERO)
                        .split(SPACE_REGEX)[1]));

                List<ElementHandle> rools = main.querySelectorAll("//li[contains(@class, 'params-list-item')]");

                responseDTO.setKids(rools.stream()
                        .filter(f -> f.innerHTML().contains("Можно с детьми"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(UNKNOWN)
                        .contains(YES));

                responseDTO.setKids(rools.stream()
                        .filter(f -> f.innerHTML().contains("Можно с животными"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(UNKNOWN)
                        .contains(YES));

                String[] year = rools.stream()
                        .filter(f -> f.innerHTML().contains("Год постройки"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(ZERO)
                        .split(EMPTY_REGEX);

                responseDTO.setYear(Long.valueOf(year.length > 1 ? year[1] : year[0]));

                responseDTO.setOwner(main.querySelector("//div[contains(@data-marker, \"seller-info/label\")]")
                        .innerText().equalsIgnoreCase("Арендодатель") ? OwnerDto.OWNER : OwnerDto.AGENT);

                String date = main.querySelector("//span[contains(@data-marker, 'item-date')]")
                        .innerText();

                String[] time = date.split(SPACE_REGEX);
                String[] splittedTime = time.length > 5 ? time[5].split(COLON) : time[4].split(COLON);
                int hours = Integer.parseInt(splittedTime[0]);
                int minutes = Integer.parseInt(splittedTime[1]);

                setDate(responseDTO, date, time, hours, minutes, false);

                if (!isAdContainsInDB(responseDTO)) adRepository.save(responseDTO.getAd());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setDate(ResponseDto responseDTO, String date, String[] time, int hours, int minutes, boolean cian) {
        LocalDate dateOfAd;
        if (Month.isMonthsContains(date)) {
            LocalDate currentDate = LocalDate.now();
            dateOfAd = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), cian ? Integer.parseInt(time[0]) : Integer.parseInt(time[2]));

            responseDTO.setDateCreated(dateOfAd.atTime(hours, minutes));
        } else if (date.contains("сегодня")) {
            dateOfAd = LocalDate.now();

            responseDTO.setDateCreated(dateOfAd.atTime(hours, minutes));
        } else {
            dateOfAd = LocalDate.now().minusDays(1);

            responseDTO.setDateCreated(dateOfAd.atTime(hours, minutes));
        }
    }

    private boolean isAdContainsInDB(ResponseDto responseDto) {
        return adRepository.existsByDateCreatedAndTitle(responseDto.getDateCreated(), responseDto.getTitle());
    }
}
