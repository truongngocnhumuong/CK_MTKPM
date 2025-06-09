package strategy;

import java.util.List;

public interface SearchStrategy<T> {
    List<T> search(String query);
}
