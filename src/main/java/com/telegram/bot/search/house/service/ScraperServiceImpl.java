package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.dto.ResponseDto;
import io.micrometer.common.util.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ScraperServiceImpl {
    /*@Value("#{'${websites.urls}'.split(',')}")
    private List<String> url;*/

    private String url = "https://saratov.cian.ru/snyat-kvartiru/";

    public Set<ResponseDto> getAds() {
        Set<ResponseDto> responseDtos = new HashSet<>();
        extractDataFromCian(responseDtos, url);
        return responseDtos;
    }

    private void extractDataFromCian(Set<ResponseDto> responseDTOS, String url) {

        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36")
                    .referrer("https://saratov.cian.ru/")
                    .followRedirects(true)
                    .get();

            Elements elements = document.getElementsByTag("article");

            for (Element ads : elements) {
                ResponseDto responseDTO = new ResponseDto();

                if (!StringUtils.isEmpty(String.valueOf(ads.attr("data-mark","OfferTitle")))) {
                    responseDTO.setTitle(ads.getElementsByAttributeValue("data-mark","OfferTitle").last().text());
                    responseDTO.setUrl(ads.getElementsByAttribute("href").first().absUrl("href"));
                    responseDTO.setPrice(ads.getElementsByAttributeValue("data-mark","MainPrice").text());

                    Elements geoLabel = ads.getElementsByAttributeValue("data-name", "GeoLabel");

                    responseDTO.setDistrict(geoLabel.get(2).text());
                    responseDTO.setStreet(geoLabel.get(3).text() + " " + geoLabel.get(4).text());

                    String[] offerSubtitles = ads.getElementsByAttributeValue("data-mark", "OfferSubtitle").text().split(",");

                    if(offerSubtitles.length > 2) {
                        responseDTO.setRooms(offerSubtitles[0]);
                        responseDTO.setFloor(offerSubtitles[2]);
                    }
                }
                if (responseDTO.getUrl() != null) responseDTOS.add(responseDTO);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
