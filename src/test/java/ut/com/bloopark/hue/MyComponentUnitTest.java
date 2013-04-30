package ut.com.bloopark.hue;

import org.junit.Test;
import com.bloopark.hue.MyPluginComponent;
import com.bloopark.hue.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}