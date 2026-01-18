package com.galapea.techblog.volunteer_matching.griddb;

import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsRequest;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsResponse;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbCloudSQLStmt;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbContainerDefinition;
import com.galapea.techblog.volunteer_matching.griddbwebapi.SQLSelectResponse;
import com.galapea.techblog.volunteer_matching.griddbwebapi.SQLUpdateResponse;
import com.galapea.techblog.volunteer_matching.griddbwebapi.SqlExecutionResult;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GridDbClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GridDbClient.class);

    private final RestClient restClient;

    public GridDbClient(@Qualifier("GridDbRestClient") final RestClient restClient) {
        this.restClient = restClient;
    }

    public Boolean checkConnection() {
        try {
            restClient.get().uri("/checkConnection").retrieve().toBodilessEntity();
            LOGGER.info("Connection to GridDBCloud successful.");
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to connect to GridDBCloud", e);
            return false;
        }
    }

    public void createContainer(final GridDbContainerDefinition containerDefinition) {
        try {
            restClient
                    .post()
                    .uri("/containers")
                    .body(containerDefinition)
                    .retrieve()
                    .toBodilessEntity();
        } catch (GridDbException e) {
            if (e.getStatus() != null && e.getStatus().value() == 409) {
                return;
            }
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to create container", e);
            throw new GridDbException("Failed to create container", HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    public void post(String uri, Object body) {
        try {
            restClient.post().uri(uri).body(body).retrieve().toBodilessEntity();
        } catch (GridDbException e) {
            throw e;
        } catch (Exception e) {
            throw new GridDbException("Failed to execute POST request", HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    /**
     * Registers rows of data into a specified GridDB container. For more details,
     * refer to the <a href=
     * "https://www.toshiba-sol.co.jp/en/pro/griddb/docs-en/v5_7/GridDB_Web_API_Reference.html#row-registration-in-a-single-container">GridDB
     * Web API Reference</a>
     *
     * @param containerName
     *            The name of the container where rows will be registered
     * @param body
     *            The data to be registered in the container
     * @throws GridDbException
     *             If there's an error during the registration process with GridDB
     *             or if the REST request fails
     */
    public void registerRows(String containerName, Object body) {
        try {
            ResponseEntity<String> result = restClient
                    .put()
                    .uri("/containers/" + containerName + "/rows")
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);
            LOGGER.info("Register row response:{}", result);
        } catch (GridDbException e) {
            throw e;
        } catch (Exception e) {
            throw new GridDbException("Failed to execute PUT request", HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    /**
     * Retrieves rows from a specified GridDB container using the provided request
     * body.
     * <p>
     * This method sends a POST request to the GridDB Cloud API to fetch rows from
     * the given container according to the parameters specified in the
     * {@link AcquireRowsRequest}. The response is mapped to an
     * {@link AcquireRowsResponse} object containing the columns, rows, and
     * pagination information.
     * </p>
     * For more details, refer to the <a href=
     * "https://www.toshiba-sol.co.jp/en/pro/griddb/docs-en/v5_7/GridDB_Web_API_Reference.html#row-acquisition-from-a-single-container">GridDB
     * Web API Reference</a>
     *
     * @param containerName
     *            the name of the GridDB container to query
     * @param requestBody
     *            the request parameters for acquiring rows (offset, limit,
     *            condition, sort, etc.)
     * @return an {@link AcquireRowsResponse} containing the result set from the
     *         container
     * @throws GridDbException
     *             if the request fails or the GridDB API returns an error
     */
    public AcquireRowsResponse acquireRows(String containerName, AcquireRowsRequest requestBody) {
        try {
            ResponseEntity<AcquireRowsResponse> responseEntity = restClient
                    .post()
                    .uri("/containers/" + containerName + "/rows")
                    .body(requestBody)
                    .retrieve()
                    .toEntity(AcquireRowsResponse.class);
            return responseEntity.getBody();
        } catch (GridDbException e) {
            throw e;
        } catch (Exception e) {
            throw new GridDbException("Failed to execute GET request", HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    public SQLSelectResponse[] select(List<GridDbCloudSQLStmt> sqlStmts) {
        try {
            ResponseEntity<SQLSelectResponse[]> responseEntity = restClient
                    .post()
                    .uri("/sql/dml/query")
                    .body(sqlStmts)
                    .retrieve()
                    .toEntity(SQLSelectResponse[].class);
            return responseEntity.getBody();
        } catch (GridDbException e) {
            throw e;
        } catch (Exception e) {
            throw new GridDbException(
                    "Failed to execute /sql/dml/query", HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    public SqlExecutionResult[] executeSqlDDL(List<GridDbCloudSQLStmt> sqlStmts) {
        try {
            ResponseEntity<SqlExecutionResult[]> responseEntity =
                    restClient.post().uri("/sql/ddl").body(sqlStmts).retrieve().toEntity(SqlExecutionResult[].class);
            return responseEntity.getBody();
        } catch (GridDbException e) {
            throw e;
        } catch (Exception e) {
            throw new GridDbException("Failed to execute SQL DDL", HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }

    public SQLUpdateResponse[] executeSQLUpdate(List<GridDbCloudSQLStmt> sqlStmts) {
        try {
            ResponseEntity<SQLUpdateResponse[]> responseEntity = restClient
                    .post()
                    .uri("/sql/dml/update")
                    .body(sqlStmts)
                    .retrieve()
                    .toEntity(SQLUpdateResponse[].class);
            return responseEntity.getBody();
        } catch (GridDbException e) {
            throw e;
        } catch (Exception e) {
            throw new GridDbException(
                    "Failed to execute /sql/dml/update", HttpStatusCode.valueOf(500), e.getMessage(), e);
        }
    }
}
