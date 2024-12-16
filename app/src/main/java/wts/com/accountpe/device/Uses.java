package wts.com.accountpe.device;

import org.simpleframework.xml.Attribute;

public class Uses {

    public Uses() {
    }

    @Attribute(name = "pi", required = false)
    public String pi;

    @Attribute(name = "pa", required = false)
    public String pa;

    @Attribute(name = "pfa", required = false)
    public String pfa;

    @Attribute(name = "bio", required = false)
    public String bio;

    @Attribute(name = "bt", required = false)
    public String bt;

    @Attribute(name = "pin", required = false)
    public String pin;

    @Attribute(name = "otp", required = false)
    public String otp;

}
