## JNDI 설정

Tomcat - MariaDB 간의 JNDI 설정하는 법을 기술

- Tomcat 내장 라이브러리를 추가하여 설정값중 패스워드부분은 암호화 처리하여 사용

1. MariaDB JDBC 드라이버 다운로드
    
    > [Connector/J | MariaDB](https://mariadb.com/kb/en/installing-mariadb-connectorj/)
    > 
2. 다운받은 jar를 TOMCAT 폴더의 lib 에 이동
3. Tomcat의 conf/context.xml 에 JNDI 설정 추가
    
    <aside>
    💡 context 설정은 Tomcat 설정에 넣을 수 있고 application 단에 넣을수도 있다. 각각의 우선순위는 다음과 같다.
    
    ① server.xml 안에 <Context.xml> 참조 - 권장하지 않음
    
    ② CATALINA_HOME/conf/ENGINE_NAME/HOST_NAME/CONTEXT_PATH.xml 참조
    
    <Host>의 속성에 xmlBase 값이 설정되지 않은 경우, 위 위치에 해당하는 file을 참조하게 됩니다. <Engine>의 name속성이 “Catalina“, <Host>의 name속성이 “localhost”이면 WebApp01의 context 설정 file의 위치는   “CATALINA_HOME/conf/Catalina/localhost/WebApp01.xml“이 된다.
    
    ③ Web application WAR, directory의 /META-INF/context.xml을 참조한다.
    
    ④ CATALINA_HOME/conf/context.xml 참조 - 다른 context file이 없는 경우
    
    </aside>
    
    ```xml
    <Context>
    ...
    <Resource name="jdbc/MyDB"
              factory="EncryptedDataSourceFactory"
              auth="Container"
              type="javax.sql.DataSource"
              maxActive="10"
              maxIdle="5"
              maxWait="10000"
              username="portal"
              password="portal"
              driverClassName="org.mariadb.jdbc.Driver"
              url="jdbc:mariadb://localhost:3306/portal"/>
    </Context>
    ```
    
4. application 단의 web.xml 설정
    
    ```xml
    <resource-ref>
    	  <description>DB Connection</description>
    	  <res-ref-name>jdbc/MyDB</res-ref-name>
    	  <res-type>javax.sql.DataSource</res-type>
    	  <res-auth>Container</res-auth>
    </resource-ref>
    ```

5. 생성된 jar 를 이용하여 평문 → 암호화 값 출력
    
    ```bash
    $ java -jar tomcat-jndi-1.0-SNAPSHOT.jar [원하는값]
    [원하는값] : 050506398eac9168640acad815f033f4
    ```
    
6. 톰켓 내장 라이브러리로 복사
    
    ```bash
    $ [TOMCAT_HOME]/lib/tomcat-jndi-1.0-SNAPSHOT.jar
    ```
    
7. Tomcat의 `<context>` 설정값중 패스워드를 암호화된 값으로 교체
    
    ```xml
    <Context>
    ...
    <Resource name="jdbc/MyDB"
              factory="EncryptedDataSourceFactory"
              auth="Container"
              type="javax.sql.DataSource"
              maxActive="10"
              maxIdle="5"
              maxWait="10000"
              username="portal"
              password="050506398eac9168640acad815f033f4"
              driverClassName="org.mariadb.jdbc.Driver"
              url="jdbc:mariadb://localhost:3306/portal"/>
    </Context>
    ```


8. jsp 생성후 호출하여 접속확인

  testDB.jsp
    
    ```html
    <%@ page import="java.sql.*"%>
    <%@ page import="javax.sql.*"%>
    <%@ page import="javax.naming.*"%>
    <%@ page contentType="text/html;charset=utf-8"%>
    <%
             Context ctx = null;
             Connection conn = null;
             Statement stmt = null;
             ResultSet rs = null;
     
             try {
                    ctx = new InitialContext();
                    DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/MyDB");
                    conn = ds.getConnection();
                    stmt = conn.createStatement();
     
                    out.println("MySQL Connection Success!");
                    out.println("<br />");
                    out.println("DB SQL Start");
     
                    out.println("<br />");
                    out.println("<br />");
     
                    rs = stmt.executeQuery("select no from TEST_TABLE LIMIT 10000");
                    out.println("<table border=1>");
                    out.println("<tr>");
                    out.println("<th>No</th>");
                    out.println("</tr>");
     
                    while(rs.next()) {
                        out.println("<tr>");
                        out.println("<td>" + rs.getString("no") + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
     
                    conn.close();
             }
             catch(Exception e){
                  out.println(e);
             }
    %>
    ```
