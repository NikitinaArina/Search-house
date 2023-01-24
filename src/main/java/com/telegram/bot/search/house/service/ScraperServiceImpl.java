package com.telegram.bot.search.house.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.telegram.bot.search.house.dto.Month;
import com.telegram.bot.search.house.dto.OwnerDto;
import com.telegram.bot.search.house.dto.RenovationDto;
import com.telegram.bot.search.house.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ScraperServiceImpl {
    @Value("${urls}")
    private String[] url;

    public Set<ResponseDto> getAds() {
        Set<ResponseDto> responseDtos = new HashSet<>();
        extractDataFromCian(responseDtos, url[0]);
        extractDataFromAvito(responseDtos, url[1]);
        return responseDtos;
    }

    private void extractDataFromCian(Set<ResponseDto> responseDTOS, String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                    .setJavaScriptEnabled(true)
                    .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36"));
            Page page = browserContext
                    .newPage();
            Page.NavigateOptions options = new Page.NavigateOptions()
                    .setTimeout(10000)
                    .setWaitUntil(WaitUntilState.NETWORKIDLE);
            page.navigate(url, options);

            List<ElementHandle> ads = page.locator("article").elementHandles();

            for (ElementHandle ad : ads) {
                ResponseDto responseDTO = new ResponseDto();

                String apartLink = ad.querySelector("//div[@data-name='LinkArea']/a[@href]").getProperty("href").toString();
                responseDTO.setUrl(apartLink);

                Page adPage = browserContext.newPage();

                adPage.navigate(apartLink, options.setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                ElementHandle main = adPage.locator("//main").elementHandle();

                ElementHandle title = main.querySelector("//div[@data-name='OfferTitle']");

                responseDTO.setTitle(title.innerText());
                responseDTO.setLocation(main.querySelector("//div[@data-name='Geo']").innerText().replace("На карте", ""));
                responseDTO.setPrice(Long.valueOf(main.querySelector("//span[@itemprop='price']").innerText()
                        .replaceFirst("\u00a0", "")
                        .split("\u00a0")[0]));
                responseDTO.setSquare(Long.valueOf(main.querySelectorAll("//div[@data-testid='object-summary-description-value']").get(0).innerText()
                        .split("\u00a0")[0]
                        .split(",")[0]));
                responseDTO.setFloor(Long.valueOf(main.querySelectorAll("//div[@data-testid='object-summary-description-info']").stream()
                        .filter(f -> f.innerHTML().contains("Этаж"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("Неизвестно")
                        .split("\s")[0]));
                responseDTO.setRooms(Long.valueOf(title.innerText().split(",")[0]
                        .split("-")[0]));

                String year = main.querySelectorAll("//div[@data-testid='object-summary-description-info']").stream()
                        .filter(f -> f.innerHTML().contains("Построен"))
                        .map(m -> m.querySelector("//div[contains(@class, 'value')]"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("0");
                responseDTO.setYear(Long.valueOf(year));

                String date = main.querySelector("//div[@data-name='OfferMeta']//div[contains(@data-name, 'OfferAdded')]").innerText();

                String[] time = date.split("\s");
                String[] splittedTime = time.length > 2 ? time[2].split(":") : time[1].split(":");
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
                    responseDTO.setKids(kids.innerText().contains("Можно"));
                } else responseDTO.setKids(false);

                ElementHandle pets = main.querySelector("//ul[@data-name='Tenants']/li[contains(@class, 'pets')]");
                if (pets != null) {
                    responseDTO.setKids(pets.innerText().contains("Можно"));
                } else responseDTO.setAnimal(false);

                String renovation = main.querySelectorAll("//article[@data-name='AdditionalFeaturesGroup']//li").stream()
                        .filter(f -> f.innerHTML().contains("Ремонт"))
                        .map(m -> m.querySelector("//span[contains(@class, 'value')]"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("Неизвестно");

                responseDTO.setRenovationDto(RenovationDto.getByRenovation(renovation));

                if (responseDTO.getUrl() != null) responseDTOS.add(responseDTO);
            }
        }
    }

    private void extractDataFromAvito(Set<ResponseDto> responseDTOS, String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                    .setJavaScriptEnabled(true)
                    .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36"));
            Page page = browserContext
                    .newPage();
            Page.NavigateOptions options = new Page.NavigateOptions()
                    .setTimeout(10000)
                    .setWaitUntil(WaitUntilState.COMMIT);
            page.navigate(url, options);

            List<ElementHandle> ads = page.querySelectorAll("//div[@data-marker='item']");
            while (ads == null || ads.isEmpty()) {
                page.reload();
                ads = page.querySelectorAll("//div[@data-marker='item']");
            }

            for (ElementHandle ad : ads) {
                ResponseDto responseDTO = new ResponseDto();

                String apartLink = "https://www.avito.ru" + ad.querySelector("//a[@itemprop='url']").getAttribute("href");
                responseDTO.setUrl(apartLink);

                Page adPage = browserContext.newPage();

                adPage.navigate(apartLink, options);
                ElementHandle main = adPage.locator("//div[contains(@class, 'style-item-view-page')]").elementHandle();

                responseDTO.setPrice(Long.valueOf(main.querySelectorAll("//span[contains(@class, 'style-price-value-main')]").get(1)
                        .innerText()
                        .split("\n")[0]
                        .replace("\u00a0", "")));
                responseDTO.setLocation(main.querySelector("//div[@itemprop='address']").innerText().replaceAll("\n", " "));
                responseDTO.setTitle(main.querySelector("//div[contains(@class, 'title-info-main')]").innerText());

                List<ElementHandle> infoList = main.querySelectorAll("//li[contains(@class, 'paramsList__item')]");

                responseDTO.setRooms(Long.valueOf(infoList.stream()
                        .filter(f -> f.innerHTML().contains("Количество комнат"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("0")
                        .split("\s")[2]));

                responseDTO.setRenovationDto(RenovationDto.getByRenovation(infoList.stream()
                        .filter(f -> f.innerHTML().contains("Ремонт"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("Неизвестно")
                        .split("\s")[1]));

                responseDTO.setSquare(Long.valueOf(infoList.stream()
                        .filter(f -> f.innerHTML().contains("Общая площадь"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("0")
                        .split("\s")[2]
                        .split("\u00a0")[0]
                        .split("\\.")[0]));

                responseDTO.setFloor(Long.valueOf(infoList.stream()
                        .filter(f -> f.innerHTML().contains("Этаж"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("0")
                        .split("\s")[1]));

                List<ElementHandle> rools = main.querySelectorAll("//li[contains(@class, 'params-list-item')]");

                responseDTO.setKids(rools.stream()
                        .filter(f -> f.innerHTML().contains("Можно с детьми"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("Неизвестно")
                        .contains("да"));

                responseDTO.setKids(rools.stream()
                        .filter(f -> f.innerHTML().contains("Можно с животными"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("Неизвестно")
                        .contains("да"));

                String[] year = rools.stream()
                        .filter(f -> f.innerHTML().contains("Год постройки"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("0")
                        .split("\u00a0");

                responseDTO.setYear(Long.valueOf(year.length > 1 ? year[1] : year[0]));

                responseDTO.setOwner(main.querySelector("//div[contains(@data-marker, \"seller-info/label\")]")
                        .innerText().equalsIgnoreCase("Арендодатель") ? OwnerDto.OWNER : OwnerDto.AGENT);

                String date = main.querySelector("//span[contains(@data-marker, 'item-date')]")
                        .innerText();

                String[] time = date.split("\s");
                String[] splittedTime = time.length > 5 ? time[5].split(":") : time[4].split(":");
                int hours = Integer.parseInt(splittedTime[0]);
                int minutes = Integer.parseInt(splittedTime[1]);

                setDate(responseDTO, date, time, hours, minutes, false);

                responseDTOS.add(responseDTO);
            }
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
}
