package com.telegram.bot.search.house.service.scraper.impl;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.telegram.bot.search.house.constants.CityConstants;
import com.telegram.bot.search.house.dto.ResponseDto;
import com.telegram.bot.search.house.dto.enums.Month;
import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import com.telegram.bot.search.house.dto.enums.RoomDto;
import com.telegram.bot.search.house.repository.AdRepository;
import com.telegram.bot.search.house.service.scraper.ScraperService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.telegram.bot.search.house.constants.Constants.*;
import static com.telegram.bot.search.house.dto.enums.RenovationDto.GRANDMOTHER;

@Service
public class ScraperServiceImpl implements ScraperService {
    private static final Logger LOGGER = LogManager.getLogger(ScraperServiceImpl.class);
    @Value("${urls}")
    private String[] url;
    private final AdRepository adRepository;
    private Counter scrapCounter;

    @Autowired
    public ScraperServiceImpl(AdRepository adRepository, MeterRegistry registry) {
        this.adRepository = adRepository;
        scrapCounter = Counter.builder("scrap_ad_counter")
                .description("Количество собранных объявлений")
                .register(registry);
    }

    @Override
    public void getAds() {
        extractDataFromCian(url[0]);
        extractDataFromAvito(url[1]);
    }

    @Override
    public boolean checkAd(String url) {
        boolean isCian = url.contains("cian");
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent(USER_AGENT));
            browserContext.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
            Page page = browserContext
                    .newPage();
            Page.NavigateOptions options = new Page.NavigateOptions()
                    .setTimeout(30000)
                    .setWaitUntil(isCian ? WaitUntilState.DOMCONTENTLOADED : WaitUntilState.LOAD);
            page.navigate(url, options);

            ElementHandle elementHandle;

            if (isCian) {
                elementHandle = page.querySelector("//div[@data-name='OfferUnpublished']");
            } else {
                elementHandle = page.querySelector("//span[@class='closed-warning-content-_f4_B']");
            }

            if (elementHandle != null) {
                adRepository.deleteByUrl(url);
                return true;
            }
        }
        return false;
    }

    private void extractDataFromCian(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent(USER_AGENT));
            browserContext.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
            Page page = browserContext
                    .newPage();
            Page.NavigateOptions options = new Page.NavigateOptions()
                    .setTimeout(30000)
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED);
            page.navigate(url, options);

            List<ElementHandle> ads = page.locator("article").elementHandles();

            for (ElementHandle ad : ads) {
                ResponseDto responseDTO = new ResponseDto();

                String apartLink = ad.querySelector("//div[@data-name='LinkArea']/a[@href]").getProperty("href").toString();
                LOGGER.info(apartLink);
                responseDTO.setUrl(apartLink);

                Page newPage = browserContext.newPage();

                newPage.navigate(apartLink, options);
                synchronized (newPage) {
                    newPage.wait(10000);
                }

                String selectorMain = "//div[@data-name='OfferCardPageLayout']";
                ElementHandle main = newPage.querySelector(selectorMain);
                if (main != null) {
                    ElementHandle title = main.querySelector("//div[@data-name='OfferTitleNew']");

                    responseDTO.setTitle(title.innerText());
                    responseDTO.setLocation(main.querySelector("//div[@data-name='Geo']").innerText().replace("На карте", ""));

                    setCity(responseDTO);

                    String date = main.querySelector("//div[@data-name='OfferMetaData']//div[contains(@data-testid, 'metadata-added-date')]").innerText();

                    String[] time = date.split(SPACE_REGEX);
                    String[] splittedTime = time.length > 2 ? time[2].split(COLON) : time[1].split(COLON);
                    int hours = Integer.parseInt(splittedTime[0]);
                    int minutes = Integer.parseInt(splittedTime[1]);

                    setDate(responseDTO, date, time, hours, minutes, true);

                    synchronized (newPage) {
                        newPage.wait(10000);
                    }
                    if (!isAdContainsInDB(responseDTO)) {

                        try {
                            responseDTO.setPrice(Long.valueOf(main.querySelector("//div[@data-name='PriceInfo']").innerText()
                                    .replaceFirst(EMPTY_REGEX, "")
                                    .split(EMPTY_REGEX)[0]));
                            responseDTO.setSquare(Long.valueOf(main.querySelectorAll("//div[@data-name='ObjectFactoidsItem']").stream()
                                    .filter(f -> f.innerHTML().contains("Общая площадь"))
                                    .map(ElementHandle::innerText)
                                    .findFirst()
                                    .orElse(ZERO)
                                    .split("Общая площадь")[1]
                                    .split(EMPTY_REGEX)[0]
                                    .split("\n")[1]));
                            responseDTO.setFloor(Long.valueOf(main.querySelectorAll("//div[@data-name='ObjectFactoidsItem']").stream()
                                    .filter(f -> f.innerHTML().contains(FLOOR))
                                    .map(ElementHandle::innerText)
                                    .findFirst()
                                    .orElse(UNKNOWN)
                                    .split(FLOOR)[1]
                                    .split(EMPTY_REGEX)[0]
                                    .split("\n")[1]
                                    .split(SPACE_REGEX)[0]));
                        } catch (NumberFormatException ignored) {

                        }

                        String rooms = title.innerText().split(COMMA)[0]
                                .split("-")[0];
                        responseDTO.setRooms(RoomDto.getByRooms(rooms));

                        String year = main.querySelectorAll("//div[@data-name='ObjectFactoidsItem']").stream()
                                .filter(f -> f.innerHTML().contains("Год постройки"))
                                .map(ElementHandle::innerText)
                                .findFirst()
                                .orElse(ZERO);
                        try {
                            responseDTO.setYear(Long.valueOf(year.split("Год постройки")[1]
                                    .split("\n")[1]));
                        } catch (Exception error) {
                            responseDTO.setYear(0L);
                        }

                        ElementHandle owner = main.querySelector("//div[@data-name='HomeownerLayout']/div[contains(@class, 'title')]");

                        if (owner != null) {
                            responseDTO.setOwner(OwnerDto.OWNER);
                        } else
                            responseDTO.setOwner(OwnerDto.AGENT);

                        main.querySelectorAll("//div[@data-name='OfferFactItem']").stream()
                                .filter(f -> f.innerHTML().contains("Условия проживания"))
                                .map(ElementHandle::innerText)
                                .findFirst()
                                .ifPresent(act -> {
                                    if (act.contains("можно с детьми и животными")) {
                                        responseDTO.setKids(true);
                                        responseDTO.setAnimal(true);
                                    } else if (act.contains("можно с детьми")) {
                                        responseDTO.setKids(true);
                                        responseDTO.setAnimal(false);
                                    } else if (act.contains("можно с животными")) {
                                        responseDTO.setAnimal(true);
                                        responseDTO.setKids(false);
                                    } else {
                                        responseDTO.setAnimal(false);
                                        responseDTO.setKids(false);
                                    }
                                });

                        String renovation = main.querySelectorAll("//div[@data-name='OfferSummaryInfoItem']").stream()
                                .filter(f -> f.innerHTML().contains(RENOVATION))
                                .map(ElementHandle::innerText)
                                .findFirst()
                                .orElse(GRANDMOTHER.getRenovation());

                        responseDTO.setRenovationType(RenovationDto.getByRenovation(renovation.equals(GRANDMOTHER.getRenovation()) ? renovation
                                : renovation.split("Ремонт")[1].split("\n")[2]));

                        adRepository.save(responseDTO.getAd());
                        scrapCounter.increment();

                        newPage.close();
                    }
                    synchronized (newPage) {
                        newPage.wait(10000);
                    }
                }
            }
            browser.close();
        } catch (InterruptedException ignored) {
        }
    }

    private static void setCity(ResponseDto responseDTO) {
        CityConstants.getCities().forEach(f -> {
            if (responseDTO.getLocation().contains(f)) {
                responseDTO.setCity(f);
            }
        });
    }

    private void extractDataFromAvito(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Browser.NewContextOptions browserContextOptions = new Browser.NewContextOptions()
                    .setUserAgent(USER_AGENT);
            BrowserContext browserContext = browser.newContext(browserContextOptions);
            browserContext.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
            Page page = browserContext
                    .newPage();
            Page.NavigateOptions options = new Page.NavigateOptions()
                    .setTimeout(30000)
                    .setWaitUntil(WaitUntilState.LOAD);
            page.navigate(url, options);

            synchronized (page) {
                page.wait(10000);
            }

            List<ElementHandle> ads = page.querySelectorAll("//div[@data-marker='item']");

            if (ads.size() == 0) {
                synchronized (page) {
                    page.wait(5000);
                }
                page.reload();

                ads = page.querySelectorAll("//div[@data-marker='item']");
            }

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

                if (main == null) {
                    synchronized (newPage) {
                        newPage.wait(5000);
                    }
                    newPage.reload();

                    main = newPage.querySelector(selectorMain);
                }

                LOGGER.info(apartLink);

                List<ElementHandle> prices = main.querySelectorAll("//span[contains(@class, 'style-price-value-main')]");
                String price = prices.get(1)
                        .innerText()
                        .split(NEW_LINE)[0]
                        .replace(EMPTY_REGEX, "");
                responseDTO.setPrice(price.contains("₽") ? Long.valueOf(price.substring(0, price.indexOf("₽"))) : Long.valueOf(price));
                responseDTO.setLocation(main.querySelector("//div[@itemprop='address']").innerText().replaceAll(NEW_LINE, " "));
                responseDTO.setTitle(main.querySelector("//div[contains(@class, 'title-info-main')]").innerText());

                setCity(responseDTO);

                List<ElementHandle> infoList = main.querySelectorAll("//li[contains(@class, 'paramsList__item')]");

                String rooms = infoList.stream()
                        .filter(f -> f.innerHTML().contains("Количество комнат"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(ZERO)
                        .split(SPACE_REGEX)[2];

                responseDTO.setRooms(RoomDto.getByRooms(rooms));

                synchronized (newPage) {
                    newPage.wait(5000);
                }

                responseDTO.setRenovationType(RenovationDto.getByRenovation(infoList.stream()
                        .filter(f -> f.innerHTML().contains(RENOVATION))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse(GRANDMOTHER.getRenovation())
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

                if (!isAdContainsInDB(responseDTO)) {
                    adRepository.save(responseDTO.getAd());
                    scrapCounter.increment();
                }

                synchronized (newPage) {
                    newPage.wait(5000);
                }
            }
        } catch (InterruptedException ignored) {
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
