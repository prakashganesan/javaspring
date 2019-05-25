package hello;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationTest {

        private Application application = new Application();

	@Test
	public void aTest() {
		assertThat(application.home(), containsString("Hello"));
	}

}
