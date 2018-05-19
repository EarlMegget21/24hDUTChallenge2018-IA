package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import algo.Client;

public class TestClient {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//TODO: initialisation d'un faux plateau de jeu ici
	}
	
	/* pour tester les autres méthodes, il faut décommenter ça, enlver le @Ignore sur les methodes et
	 * mettre @Ignore sur testJouerUnTour()
	 */
	@Before
	public void setUp(){
		//TODO: initialisation des situations ici
	}

	@Test
	@Ignore
	public void test() {
		//exemple de méthode test
	}

}
