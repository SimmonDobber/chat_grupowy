import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable
{
    public Date time;
    public String content;

    public Message(Date time, String content) {
        this.time = time;
        this.content = content;
    }

    public Message(Message message) {
        this.time = message.time;
        this.content = message.content;
    }
}
