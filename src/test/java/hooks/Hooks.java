package hooks;

import io.cucumber.java.Before;

import static base_urls.MedunnaBaseUrl.setUp;

public class Hooks {

    @Before("@api")
    public void beforeApi(){

        setUp();

    }


}
