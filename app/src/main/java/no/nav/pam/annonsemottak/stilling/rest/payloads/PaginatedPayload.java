package no.nav.pam.annonsemottak.stilling.rest.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Extends {@link EtaggedPayload} to add pagination meta data.
 *
 * @param <T> As for {@link EtaggedPayload}.
 */
public class PaginatedPayload<T> extends EtaggedPayload<List<T>> {

    private final Page<T> page;
    private final PageMeta meta;

    public PaginatedPayload(Page<T> page) {
        super(page.getContent());
        this.page = page;
        this.meta = new PageMeta();
    }

    @Override
    public PageMeta getMeta() {
        return meta;
    }

    /**
     * Adds some meta data related to paging, in a separate field {@code paging}. Contents were originally based on
     * Jackson deserialization of {@code Page<T>}, so feel free to remove fields here if not needed by clients (some
     * might be useful, though).
     */
    public class PageMeta extends EtagMeta {

        private final PageMetaContents contents;

        private PageMeta() {
            contents = new PageMetaContents();
        }

        @JsonProperty("paging")
        public PageMetaContents getPageMetaContents() {
            return contents;
        }

        public class PageMetaContents {

            private PageMetaContents() {
            }

            @JsonProperty("totalElements")
            public long getTotalElements() {
                return page.getTotalElements();
            }

            @JsonProperty("totalPages")
            public int totalPages() {
                return page.getTotalPages();
            }

            @JsonProperty("last")
            public boolean isLast() {
                return page.isLast();
            }

            @JsonProperty("size")
            public int getSize() {
                return page.getSize();
            }

            @JsonProperty("number")
            public int getNumber() {
                return page.getNumber();
            }

            @JsonProperty("numberOfElements")
            public int getNumberOfElements() {
                return page.getNumberOfElements();
            }

            @JsonProperty("sort")
            public Sort getSort() {
                return page.getSort();
            }

        }

    }
}
