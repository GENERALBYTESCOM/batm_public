import java.math.BigDecimal;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CMCQuotes;
public class CMCData {
    private String id;
    private String name;
    private String symbol;
    private String website_slug;
    private BigDecimal rank;
    private BigDecimal circulating_supply;
    private BigDecimal total_supply;
    private BigDecimal max_supply;
    private CMCQuotes quotes;
    private long last_updated;
}