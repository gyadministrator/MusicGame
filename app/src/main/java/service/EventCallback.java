package service;

public interface EventCallback<T> {
    void onEvent(T t);
}
