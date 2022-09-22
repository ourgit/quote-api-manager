package myannotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by win7 on 2016/8/13.
 */
public class DateMDSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (null != value) jsonGenerator.writeString(formatToYMDHMSBySecond(value));
    }

    /**
     * 秒数转化为YYYY-MM-DD
     *
     * @param timeStamp
     * @return
     */
    public String formatToYMDHMSBySecond(long timeStamp) {
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("MM-dd");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }
}
