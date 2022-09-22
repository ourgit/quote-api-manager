package models.admin;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.Column;

public class Meta {

    @Column(name = "title")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String title = "";

    @Column(name = "icon")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String icon = "";

    @Column(name = "no_cache")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public boolean noCache;

}
