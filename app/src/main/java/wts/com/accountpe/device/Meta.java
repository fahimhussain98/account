package wts.com.accountpe.device;

import org.simpleframework.xml.Attribute;

public class Meta {

    public Meta() {
    }

    @Attribute(name = "udc", required = false)
    public String udc;

    @Attribute(name = "rdsId", required = false)
    public String rdsId;

    @Attribute(name = "rdsVer", required = false)
    public String rdsVer;

    @Attribute(name = "dpId", required = false)
    public String dpId;

    @Attribute(name = "dc", required = false)
    public String dc;

    @Attribute(name = "mi", required = false)
    public String mi;

    @Attribute(name = "mc", required = false)
    public String mc;

}
