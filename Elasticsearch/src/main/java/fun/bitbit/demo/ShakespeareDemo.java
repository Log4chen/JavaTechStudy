package fun.bitbit.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.GetSourceRequest;
import org.elasticsearch.client.core.GetSourceResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;

/**
 * Description:
 * Date: 2024/4/14 17:56
 */
@Slf4j
public class ShakespeareDemo {
    public static void main(String[] args) throws IOException {
        // build client
        RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create("http://localhost:9200")).setHttpClientConfigCallback(httpClientBuilder -> {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "CHvCDrHa+r6xgR9ka_RW"));
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        });
        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);

        // 新增
        IndexRequest indexRequest = new IndexRequest("posts");
        indexRequest.id("2");
        String jsonString = "{" +
                "\"user\":\"tonny\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        indexRequest.source(jsonString, XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        log.info("Indexed with version " + indexResponse.getVersion());

        // 查询指定id的doc
        GetRequest getRequest = new GetRequest("posts").id("2");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        log.info("Get response: {}", getResponse.getSourceAsString());

        // 只查询 _source
        GetSourceRequest getSourceRequest = new GetSourceRequest("posts", "1");
        GetSourceResponse getSourceResponse = client.getSource(getSourceRequest, RequestOptions.DEFAULT);
        log.info("get source response:{}", getSourceResponse.getSource().toString());

        // 更新
        UpdateRequest updateRequest = new UpdateRequest("posts", "1");
        jsonString = "{" +
                "\"updated\":\"2017-01-01\"," +
                "\"reason\":\"daily update\"" +
                "}";
        updateRequest.doc(jsonString, XContentType.JSON);
        client.update(updateRequest, RequestOptions.DEFAULT);

        // 搜索
        SearchRequest searchRequest = new SearchRequest("shakespeare");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.termQuery("speaker", "FALSTAFF"));
        searchSourceBuilder.query(QueryBuilders.termQuery("text_entry", "street"));
//        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC)); // Sort descending by _score (the default)
        searchSourceBuilder.sort(new FieldSortBuilder("_id").order(SortOrder.ASC)); // Also sort ascending by _id field
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(12);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        log.info("searchResponse:{}", searchResponse);

        testScroll(client);

        client.close();
    }

    /**
     * from + size 翻页，只能翻到第1万条记录
     * shakespeare索引下有111396个doc
     */
    public static void testScroll(RestHighLevelClient client) throws IOException {
        int batchSize = 10000;
        TimeValue keepTimeValue = TimeValue.timeValueSeconds(30);
        SearchRequest searchRequest = new SearchRequest("shakespeare");
        searchRequest.scroll(keepTimeValue);
        searchRequest.source(SearchSourceBuilder.searchSource().query(QueryBuilders.matchAllQuery()).from(0).size(batchSize));

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHits hits = searchResponse.getHits();
        System.out.println(hits.getHits()[hits.getHits().length - 1].getSourceAsString());

        while (hits.getHits().length != 0) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(keepTimeValue);
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            hits = searchResponse.getHits();
            if (hits.getHits().length != 0) {
                System.out.println(hits.getHits()[hits.getHits().length - 1].getSourceAsString());
                System.out.println();
            }
        }
        // 时间过期了，也会自动clear
        ClearScrollRequest request = new ClearScrollRequest();
        request.addScrollId(scrollId);
        ClearScrollResponse response = client.clearScroll(request, RequestOptions.DEFAULT);
    }

}
