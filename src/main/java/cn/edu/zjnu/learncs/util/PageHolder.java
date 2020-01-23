package cn.edu.zjnu.learncs.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageHolder<T> implements Page<T>, Streamable<T> {
    private final long total;
    private final PageRequest pageRequest;
    private final List<T> content = new ArrayList<>();

    public PageHolder() {
        this.pageRequest = PageRequest.of(0, 1);
        this.total = 0;
    }

    public PageHolder(List<T> source, PageRequest pageRequest) {
        this.pageRequest = pageRequest;
        this.total = source.size();
        this.content.addAll(source);
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return new PageHolder<>(this.stream().map(converter::apply).collect(Collectors.toList()), pageRequest);
    }

    @Override
    public long getTotalElements() {
        return total;
    }

    /**
     * @return start page index 0
     */
    @Override
    public int getNumber() {
        if (pageRequest.getPageNumber() >= getTotalPages()) {
            return getTotalPages() - 1;
        }
        return pageRequest.getPageNumber();
    }

    @Override
    public int getSize() {
        return pageRequest.getPageSize();
    }

    @Override
    public int getNumberOfElements() {
        return getContent().size();
    }

    @Override
    public int getTotalPages() {
        return getSize() == 0 ? 1 : Math.max(1, (int) Math.ceil((double) getTotalElements() / (double) getSize()));
    }

    public int getFirstElementOnPage() {
        return (getSize() * getNumber());
    }

    @Override
    public List getContent() {
        return content.subList(getFirstElementOnPage(), getLastElementOnPage() + 1);
    }

    @Override
    public boolean hasContent() {
        return getContent().size() > 0;
    }

    @Override
    public Sort getSort() {
        return pageRequest.getSort();
    }

    @Override
    public boolean isFirst() {
        return !hasPrevious();
    }

    @Override
    public boolean isLast() {
        return !hasNext();
    }

    @Override
    public boolean hasNext() {
        return getNumber() + 1 < getTotalPages();
    }

    @Override
    public boolean hasPrevious() {
        return getNumber() > 0;
    }

    @Override
    public Pageable nextPageable() {
        return hasNext() ? pageRequest.next() : Pageable.unpaged();
    }

    @Override
    public Pageable previousPageable() {
        return hasPrevious() ? pageRequest.previousOrFirst() : Pageable.unpaged();
    }

    private int getLastElementOnPage() {
        int endIndex = (getNumber() + 1) * getSize();
        return (int) ((endIndex > getTotalElements() ? getTotalElements() : endIndex) - 1);
    }

    @Override
    public Iterator<T> iterator() {
        return getContent().iterator();
    }
}
