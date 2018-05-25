import org.junit.Test;

public class KafkaTest {

    @Test
    public void clearTest() {
        String args[] =new String[1];
        args[0] = "clear";

        AppTest.main(args);
    }

    @Test
    public void reloadTest() {
        String args[] =new String[2];
        args[0] = "reload";
        args[1] = "2017122900000032";

        AppTest.main(args);
    }


}
