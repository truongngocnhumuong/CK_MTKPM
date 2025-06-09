package strategy;

import java.util.List;

public class SearchContext<T> {
    private SearchStrategy<T> strategy;

    public void setStrategy(SearchStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public List<T> executeSearch(String query) {
        if (strategy == null) {
            throw new IllegalStateException("Chưa chọn chiến lược tìm kiếm.");
        }
        return strategy.search(query);
    }
}
