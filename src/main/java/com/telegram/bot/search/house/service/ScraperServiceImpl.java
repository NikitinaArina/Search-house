package com.telegram.bot.search.house.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
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
                responseDTO.setPrice(main.querySelector("//span[@itemprop='price']").innerText());
                responseDTO.setSquare(main.querySelectorAll("//div[@data-testid='object-summary-description-value']").get(0).innerText());
                responseDTO.setFloor(main.querySelectorAll("//div[@data-testid='object-summary-description-info']").stream()
                        .filter(f -> f.innerHTML().contains("Этаж"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("Неизвестно")
                        .replace("\n", " "));
                responseDTO.setRooms(title.innerText().split(",")[0]);

                String year = main.querySelectorAll("//div[@data-testid='object-summary-description-info']").stream()
                        .filter(f -> f.innerHTML().contains("Построен"))
                        .map(m -> m.querySelector("//div[contains(@class, 'value')]"))
                        .map(ElementHandle::innerText)
                        .findFirst()
                        .orElse("2000");
                responseDTO.setYear(year);

                String date = main.querySelector("//div[@data-name='OfferMeta']//div[contains(@data-name, 'OfferAdded')]").innerText();
                String time = date.substring(date.indexOf(",") + 1).replace(" ", "");

                String[] splittedTime = time.split(":");
                responseDTO.setDateCreated(LocalDate.now().atTime(Integer.parseInt(splittedTime[0]), Integer.parseInt(splittedTime[1])));

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
}
