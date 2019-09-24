package no.nav.pam.annonsemottak.receivers.amedia;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

class AmediaFieldTransformer {

    static final String IKKE_OPPGITT = "Ikke oppgitt";

    String finnSted(String sted) {
        if (StringUtils.isBlank(sted)) {
            return IKKE_OPPGITT;
        }
        Matcher matchEtterSisteSlash = Pattern.compile(".*/\\s*(.*)").matcher(sted);

        return matchEtterSisteSlash.find()
            ? matchEtterSisteSlash.group(1)
            : sted;

    }

    String reservefelt(String... tekst) {
        return Arrays.stream(tekst)
            .filter(StringUtils::isNotBlank)
            .findFirst()
            .orElse("Ikke oppgitt");
    }

    String hentListeSomStreng(JsonNode node) {
        if (node == null) {
            return AmediaFieldTransformer.IKKE_OPPGITT;
        }
        return node.toString();
    }

    List<String> hentListeSomStrenger(JsonNode node) {
        if (node == null) {
            return emptyList();
        }
        return Lists.newArrayList(node.iterator()).stream()
                .map(JsonNode::toString)
                .collect(Collectors.toList());
    }

    List<JsonNode> hentListeSomJsonnoder(JsonNode node) {
        if (node == null) {
            return emptyList();
        }
        return Lists.newArrayList(node.iterator());
    }

}