package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.lucene.index.IndexReader;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE;

@SpringBootTest
public class HotelDocumentTest {

    @Autowired
    private IHotelService hotelService;

    private RestHighLevelClient client;


    @Test
    public void testAddDocument() throws IOException {

        // 根据id查询酒店数据
        Hotel hotel = hotelService.getById(45845L);

        // 转换为文档类型
        HotelDoc hotelDoc = new HotelDoc(hotel);

        // 1准备Request对象
        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        // 2准备Json文档
        request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
        // 3发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    public void existsHotelIndex() throws IOException {

        // 1创建request对象
        GetIndexRequest request = new GetIndexRequest("hotel");
        // 3发送请求
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println( exists ? "存在" : "不存在");
    }

    @BeforeEach
    public void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(HttpHost.create(
                "http://192.168.19.128:9200"
        )));
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.client.close();
    }
}
