import com.company.GoodIo;
import com.company.Main;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
Setting up JUnit with IntelliJ IDEA

1. Create and setup a "tests" folder
    - In the Project sidebar on the left, right-click your project and do New > Directory.  Name it "test" or whatever you like.
    - Right-click the folder and choose "Mark Directory As > Test Source Root".
2. Adding JUnit library
    - Right-click your project and choose "Open Module Settings" or hit F4.  (Alternatively, File > Project Structure, Ctrl-Alt-Shift-S is probably the "right" way to do this)
    - Go to the "Libraries" group, click the little green plus (look up), and choose "From Maven...".
    - Search for "junit" -- you're looking for something like "junit:junit:4.11".
    - Check whichever boxes you want (Sources, JavaDocs) then hit OK.
    - Keep hitting OK until you're back to the code.
3. Write your first unit test
    - Right-click on your test folder, "New > Java Class", call it whatever, e.g. MyFirstTest.
    - Write a JUnit test
4. Run your tests
    - Right-click on your test folder and choose "Run 'All Tests'".  Presto, testo.
    - To run again, you can either hit the green "Play"-style button that appeared in the new section that popped on the bottom of your window, or you can hit the green "Play"-style button in the top bar.
 */
public class MainTest {

    @Before
    public void init() {
        Main.initController();
    }

    @Test
    public void firstTest() {
        Class< Main > obj = Main.class;
        for( Method method : obj.getDeclaredMethods() ) {
            if( method.isAnnotationPresent( GoodIo.class ) ) { //  Main.readFile(  );
                try {
                    method.invoke( obj, (Object)new String[ 0 ] ); // we check this static method if it really lives up to GoodIo
                } catch( IllegalAccessException e ) {
                    e.printStackTrace();
                } catch( InvocationTargetException e ) {
                    if( e.getCause() instanceof IOException ) {
                        Assert.fail();
                    }
                }
            }
        }
        Assert.assertTrue( true );
    }
}
