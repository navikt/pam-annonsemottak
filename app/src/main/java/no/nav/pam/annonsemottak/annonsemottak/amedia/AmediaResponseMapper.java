package no.nav.pam.annonsemottak.annonsemottak.amedia;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import no.nav.pam.annonsemottak.stilling.Stilling;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper respons fra Amedia til en liste med stillinger
 */
class AmediaResponseMapper {

    static List<Stilling> mapResponse(
        JsonNode amediaResponse) {
        JsonNode hits = amediaResponse.path("hits").path("hits");
        List<JsonNode> hitlist = Lists.newArrayList(hits.iterator());

        return hitlist.stream()
            .map(h -> new AmediaStillingMapper(h).getStilling())
            .collect(Collectors.toList());
    }

    static List<String> mapEksternIder(JsonNode amediaResponse) {
        JsonNode hits = amediaResponse.path("hits").path("hits");
        List<JsonNode> hitlist = Lists.newArrayList(hits.iterator());

        return hitlist.stream()
            .map(h -> text(h.path("_id")))
            .collect(Collectors.toList());
    }

    static String text(JsonNode node) {
        return node == null || node.asText().equals("null") ? "" : node.asText();
    }

}
