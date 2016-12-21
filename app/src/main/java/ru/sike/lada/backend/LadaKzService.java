package ru.sike.lada.backend;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.sike.lada.backend.json.Category;
import ru.sike.lada.backend.json.NewsFull;
import ru.sike.lada.backend.json.NewsShort;

public interface LadaKzService {

    @GET("index.php?action=getCats")
    Call<List<Category>> getCategories();

    @GET("index.php?action=getPosts")
    Call<List<NewsShort>> getNewsShort(
            @Query("cat") long categoryId,
            @Query("postFrom") int fetchFrom,
            @Query("postLimit") int fetchCount);

    @GET("index.php?action=getPost")
    Call<List<NewsFull>> getNewsFull(
            @Query("id") long newId);

    @GET("index.php?action=getMainPosts")
    Call<List<NewsShort>> getMainNewsShort(
            @Query("postFrom") int fetchFrom,
            @Query("postLimit") int fetchCount);
}
