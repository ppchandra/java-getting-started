/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.example.response.HerokuResponse;
import com.example.response.Result;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${test-app.url}")
  private String testUrl;

  @Value("${test-app2.url}")
  private String testUrl2;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }

  @RequestMapping(value = "/account")
  public @ResponseBody
  Result saveSfContact(@RequestParam(name = "id")String id) {
    Result result = new Result();
    List<HerokuResponse> herokuResult = new ArrayList<>();
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("select * from salesforcecgoconnect.account where salesforcecgoconnect.account.recordtypeid = '" + id + "'");

      while (rs.next()){
        HerokuResponse herokuResponse = new HerokuResponse();
        herokuResponse.setId(rs.getString("recordtypeid"));
        herokuResponse.setName(rs.getString("name"));
        herokuResult.add(herokuResponse);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    result.setResponse(herokuResult);
    return result;
  }

  @RequestMapping(value = "/af-account")
  public @ResponseBody
  ResponseEntity<Result> getAfAccount(@RequestParam(name = "id")String id) {
    RestTemplate restTemplate = new RestTemplate();
    String formattedQuoteUrl = MessageFormat.format(testUrl, id);
    return restTemplate.getForEntity(formattedQuoteUrl, Result.class);
  }

  @RequestMapping(value = "/cp-account")
  public @ResponseBody
  ResponseEntity<Result> getCpAccount(@RequestParam(name = "id")String id) {
    RestTemplate restTemplate = new RestTemplate();
    String formattedQuoteUrl = MessageFormat.format(testUrl2, id);
    return restTemplate.getForEntity(formattedQuoteUrl, Result.class);
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
