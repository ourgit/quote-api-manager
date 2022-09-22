package actor;

import java.io.Serializable;

public class ManageProtocol implements Serializable {


    private static final long serialVersionUID = 8880942309217592333L;

    public static class CHANNEL_MSGS implements Serializable {
        public String content;

        public CHANNEL_MSGS(String content) {
            this.content = content;
        }
    }

}
