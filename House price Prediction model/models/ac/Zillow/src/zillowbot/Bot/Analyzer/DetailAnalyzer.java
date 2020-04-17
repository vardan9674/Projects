package zillowbot.Bot.Analyzer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import zillowbot.Entities.Data;
import zillowbot.Entities.Estate;
import zillowbot.Entities.Option;
import zillowbot.Entities.School;
import zillowbot.Util.Util;

import java.util.List;
import java.util.Objects;

public class DetailAnalyzer {

    private static class Fact{
        String key;
        String value;
        String valueType;

        Fact(String key, String value, String valueType) {
            this.key = key;
            this.value = value;
            this.valueType = valueType;
        }
    }

    private static ObservableList<Option> getOptions(Element element, String status) {
        ObservableList<Option> result = FXCollections.observableArrayList();
        try {
            List<Node> nodes = element.childNodes();
            for (int i = 1; i < nodes.size(); i++) {
                Option option;
                Node node = nodes.get(i);
                Document document = Jsoup.parse(node.outerHtml());
                Element header = document.body().getElementsByClass("bedroom-group-header").first();
                if (header == null) continue;
                Element estateType = header.getElementsByTag("strong").first();
                String type = estateType.text();
                int bedrooms = Util.getDigits(type);
                Element content = document.body().getElementsByClass("bedroom-group-content").first();
                if (content != null) {
                    double bathrooms;
                    int areaSpace;
                    int price;
                    Elements floorPlans = content.getElementsByClass("zsg-media-bd");
                    for (Element floorPlan : floorPlans) {
                        Element titleElement= floorPlan.getElementsByClass("floorplan-title").first();
                        if(titleElement == null) continue;
                        String[] txt = titleElement.text().split("·");
                        bathrooms = Util.getDouble(txt[1]);
                        areaSpace = Util.getDigits(txt[2]);
                        Elements units = floorPlan.getElementsByClass("individual-unit");
                        for(Element unit:units){
                            Element priceElement = unit.getElementsByClass("floorplan-unit-price").first();
                            if (priceElement == null) {
                                price = -1;
                            } else {
                                price = Util.getDigits(priceElement.text());
                            }
                            Element unitNumberElement = unit.getElementsByClass("floorplan-unit-number").first();
                            String unitNumber = "";
                            if (unitNumberElement != null) {
                                unitNumber=unitNumberElement.text();
                            }
                            option = new Option(status, type, bedrooms, bathrooms, areaSpace, price, unitNumber);
                            result.add(option);
                        }
                    }
                    if (floorPlans.size() == 0) {
                        Elements units = content.getElementsByClass("individual-unit");
                        for (Element unit : units) {
                            Element bathElement = unit.getElementsByClass("individual-unit-baths").first();
                            if (bathElement == null) {
                                bathrooms = 0.0;
                            } else {
                                bathrooms = Util.getDouble(bathElement.text());
                            }
                            Element priceElement = unit.getElementsByClass("individual-unit-price").first();
                            if (priceElement == null) {
                                price = -1;
                            } else {
                                price = Util.getDigits(priceElement.text());
                            }
                            Element sqftElement = unit.getElementsByClass("individual-unit-sqft").first();
                            if (sqftElement == null) {
                                areaSpace = -1;
                            } else {
                                areaSpace = Util.getDigits(sqftElement.text());
                            }
                            Element unitNumberElement = unit.getElementsByClass("individual-unit-number").first();
                            String unitNumber = "";
                            if (unitNumberElement != null) {
                                unitNumber=unitNumberElement.text();
                            }
                            option = new Option(status, type, bedrooms, bathrooms, areaSpace, price, unitNumber);
                            result.add(option);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static void getOptions(Document document,Estate estate) {
        if (estate == null) {
            return;
        }
        try {
            Element panelForRent = document.body().getElementById("units-panel-for-rent");
            if (panelForRent != null) {
                estate.RentOptions = getOptions(panelForRent, "rent");
            }
            Element panelForSale = document.body().getElementById("units-panel-for-sale");
            if (panelForSale != null) {
                estate.RentOptions = getOptions(panelForSale, "sale");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getSchools(Document document, Estate estate) {
        estate.Schools.clear();
        try {
            Elements schools = document.body().getElementsByClass("nearby-schools-info");
            for (Element school : schools) {
                Element nameElement = school.getElementsByClass("school-name").first();
                String name = "";
                if (nameElement != null) {
                    name = nameElement.text();
                }
                if (Objects.equals(name, "")) continue;
                Element gradesElement = school.getElementsByClass("nearby-schools-grades").first();
                String grades = "";
                if (gradesElement != null) {
                    grades = gradesElement.text();
                }
                Element distanceElement = school.getElementsByClass("nearby-schools-distance").first();
                double distance = 0.0;
                if (distanceElement != null) {
                    distance = Util.getDouble(distanceElement.text());
                }
                estate.Schools.add(new School(name, grades, distance));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getDescription(Document document,Estate estate) {
        try {
            Element descriptionElement = document.body().getElementById("home-description-container");
            if (descriptionElement != null) {
                estate.setDescription(descriptionElement.text());
            } else {
                descriptionElement = document.body().select("div[data-js=bdp-description]").first();
                if (descriptionElement != null) {
                    estate.setDescription(descriptionElement.text());
                } else {
                    estate.setDescription("");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Fact getFactData(Element fact) {
        Element keyElement = fact.getElementsByClass("fact-label").first();
        String key = "";
        if (keyElement != null) {
            key = keyElement.text();
        }
        Element valueElement = fact.getElementsByClass("fact-value").first();
        String value = "";
        if (valueElement != null) {
            value = valueElement.text();
        }
        String valueType = "string";
        if (!Objects.equals(Util.getDigits(value, false),"")) {
            valueType = "number";
        }
        return new Fact(key,value,valueType);
    }

    private static void getFact(Document document, Estate estate) {
        if (estate == null) {
            return;
        }
        estate.KV1.clear();
        Element factElement=document.body().getElementsByClass("hdp-facts").first();
        String title = "";
        if (factElement != null) {
            Element head = factElement.getElementsByClass("home-details-facts-features-section-heading").first();
            if (head != null) {
                title = head.text();
                estate.KV1_Title = title;
            }
            Elements facts=factElement.getElementsByClass("fact-group");
            for (Element fact : facts) {
                Fact f = getFactData(fact);
                estate.KV1.add(new Data("Facts",title, f.key, f.value, f.valueType));
            }
        }
    }

    private static void getHomeDetails(Document document, Estate estate) {
        if (estate == null) {
            return;
        }
        estate.KV2_Title = "Details";
        Elements details = document.body().getElementsByClass("home-details-facts-category-group-container");
        if (details != null) {
            for (Element detail : details) {
                Element categoryGroupNameElement= detail.getElementsByClass("category-group-name").first();
                String categoryGroupName = "";
                if (categoryGroupNameElement != null) {
                    categoryGroupName = categoryGroupNameElement.text();
                }
                Elements categories = detail.getElementsByClass("category-container");
                for (Element category : categories) {
                    Element categoryNameElement = category.getElementsByClass("category-name").first();
                    String factName = "";
                    if (categoryNameElement != null) {
                        factName = categoryNameElement.text();
                    }
                    Elements facts = category.getElementsByClass("fact-container");
                    String factData = "";
                    String seprator = "";
                    for (Element fact : facts) {
                        Fact f = getFactData(fact);
                        String column = ":";
                        if(f.key.contains(":"))
                            column = "";
                        factData += seprator + f.key + column + f.value;
                        seprator = " | ";
                    }
                    estate.KV2.add(new Data("Details",categoryGroupName,factName,factData,"string2"));
                }
            }
        }
        Element buildingFacts = document.body().getElementsByClass("building-facts").first();
        if (buildingFacts != null) {
            Elements attrs = buildingFacts.getElementsByClass("building-attrs-group");
            for (Element attr : attrs) {
                String categoryName = "";
                if (attr.childNodeSize()>0) {
                    categoryName = ((Element)attr.childNodes().get(0)).text();
                }
                Elements facts = attr.getElementsByTag("li");
                String seprator = "";
                String value = "";
                for (Element fact : facts) {
                    value += seprator + fact.text();
                    seprator = " | ";
                }
                estate.KV2.add(new Data("Details","Details", categoryName, value, "string2"));
            }
        }
    }

    private static String getTags(Element element) {
        Elements tags = element.getElementsByTag("li");
        String result = "";
        String seprator = "";
        for (Element tag : tags) {
            result += seprator + tag.text();
            seprator = " ; ";
        }
        return result;
    }

    private static void getAmenities(Document document, Estate estate) {
        if (estate == null) {
            return;
        }
        estate.KV3.clear();
        estate.KV3_Title = "Amenities";
        Element amenities = document.body().getElementsByClass("Amenities").first();
        if (amenities != null) {
            for(int i=0;i<amenities.childNodeSize();i++) {
                Element element = (Element) amenities.childNodes().get(i);
                Element titleElement = element.getElementsByClass("Amenities__Title").first();
                String title = "";
                if (titleElement != null) {
                    title = titleElement.text();
                }
                Elements titledSections = element.getElementsByClass("Amenities__Titled-Section");
                String value = "";
                if (titledSections.size() > 0) {
                    String seprator = "";
                    for (Element section : titledSections) {
                        Element sectionTitle = section.getElementsByClass("Amenities__Section-Title").first();
                        String valueHead = "";
                        String column = "";
                        if (sectionTitle != null) {
                            valueHead = sectionTitle.text();
                            column = " : ";
                        }
                        String value2 = valueHead+column+getTags(section);
                        if (!Objects.equals(value2, "")) {
                            value += seprator + value2;
                            seprator = " ### ";
                        }
                        String[] x = value.split(seprator);
                        x[0]+="";
                    }
                } else {
                    value = getTags(element);
                }
                if (!Objects.equals(title, "") && !Objects.equals(value, "")) {
                    estate.KV3.add(new Data("Amenities",title, title, value, "string2"));
                }
            }
        }
    }

    public static void Analyze(Document document, Estate estate){
        getOptions(document, estate);
        getSchools(document, estate);
        getDescription(document, estate);
        getFact(document, estate);
        getHomeDetails(document, estate);
        getAmenities(document, estate);
        if (Objects.equals(estate.getType(), "sale")) {
            estate.FactAndFeatures = SaleDetailAnalyzer.getFacts(document);
        }
    }
}
