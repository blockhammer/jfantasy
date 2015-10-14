package com.fantasy.framework.util;

import com.fantasy.framework.util.jackson.JSON;
import com.fantasy.framework.util.userstamp.Decoder;
import com.fantasy.framework.util.userstamp.Encoder;
import com.fantasy.framework.util.userstamp.UserResult;
import com.fantasy.framework.util.userstamp.UserStamp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class UserStampTest {

    private static final Log LOG = LogFactory.getLog(UserStampTest.class);

    @Test
    public void decode() {
        UserResult result = Decoder.decode("vAcTChtxIcsgJAnI");//vAcTChtxVAsCVCHe
        assert result != null;
        assert result.checkPassword("1231245");
        LOG.debug(JSON.serialize(result));
    }

    @Test
    public void encode() {
        UserStamp stamp = Encoder.encode(1, 1123, "1231245",3,3);
        LOG.debug(JSON.serialize(stamp) + "=>" + stamp);
        LOG.debug(JSON.serialize(Decoder.decode(stamp.toString())));
    }

}
