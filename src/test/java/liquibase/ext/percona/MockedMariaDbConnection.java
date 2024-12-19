package liquibase.ext.percona;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.mariadb.jdbc.BasePreparedStatement;
import org.mariadb.jdbc.Configuration;
import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.HostAddress;
import org.mariadb.jdbc.Statement;
import org.mariadb.jdbc.client.Client;
import org.mariadb.jdbc.client.ColumnDecoder;
import org.mariadb.jdbc.client.Completion;
import org.mariadb.jdbc.client.Context;
import org.mariadb.jdbc.client.ReadableByteBuf;
import org.mariadb.jdbc.client.ServerVersion;
import org.mariadb.jdbc.export.ExceptionFactory;
import org.mariadb.jdbc.export.Prepare;
import org.mariadb.jdbc.message.ClientMessage;
import org.mariadb.jdbc.message.server.util.ServerVersionUtility;

public class MockedMariaDbConnection {

    public static java.sql.Connection create(Configuration config) throws SQLException {
        Connection mariadbConnection = new Connection(config, null, new NoopMariaDbClient(config));
        return mariadbConnection;
    }

    private static class NoopMariaDbClient implements Client {
        private Configuration config;

        public NoopMariaDbClient(Configuration config) {
            this.config = config;
        }

        @Override
        public Context getContext() {
            return new MockedContext(config);
        }

        @Override
        public ExceptionFactory getExceptionFactory() {
            return new ExceptionFactory(this.config, null);
        }

        @Override
        public List<Completion> execute(ClientMessage message, boolean canRedo) throws SQLException {
            return null;
        }

        @Override
        public List<Completion> execute(ClientMessage message, Statement stmt, boolean canRedo) throws SQLException {
            return null;
        }

        @Override
        public List<Completion> execute(ClientMessage message, Statement stmt, int fetchSize, long maxRows,
                int resultSetConcurrency, int resultSetType, boolean closeOnCompletion, boolean canRedo)
                throws SQLException {
            return null;
        }

        @Override
        public List<Completion> executePipeline(ClientMessage[] messages, Statement stmt, int fetchSize, long maxRows,
                int resultSetConcurrency, int resultSetType, boolean closeOnCompletion, boolean canRedo)
                throws SQLException {
            return null;
        }

        @Override
        public void readStreamingResults(List<Completion> completions, int fetchSize, long maxRows,
                int resultSetConcurrency, int resultSetType, boolean closeOnCompletion) throws SQLException {
        }

        @Override
        public void closePrepare(Prepare prepare) throws SQLException {
        }

        @Override
        public void abort(Executor executor) throws SQLException {
        }

        @Override
        public void close() throws SQLException {
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
        }

        @Override
        public int getSocketTimeout() {
            return 0;
        }

        @Override
        public void setSocketTimeout(int milliseconds) throws SQLException {
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public boolean isPrimary() {
            return false;
        }

        @Override
        public HostAddress getHostAddress() {
            return null;
        }

        @Override
        public String getSocketIp() {
            return null;
        }
    }

    private static class MockedContext implements Context {
        private Configuration config;

        public MockedContext(Configuration config) {
            this.config = config;
        }

        @Override
        public Configuration getConf() {
            return config;
        }

        @Override
        public boolean canUseTransactionIsolation() {
            return false;
        }

        @Override
        public ServerVersion getVersion() {
            return new ServerVersionUtility("0", false);
        }

        @Override
        public void setWarning(int warning) {
        }

        @Override
        public void setServerStatus(int serverStatus) {
        }

        @Override
        public void setDatabase(String database) {
        }

        @Override
        public void resetStateFlag() {
        }

        @Override
        public void resetPrepareCache() {
        }

        @Override
        public boolean isEofDeprecated() {
            return false;
        }

        @Override
        public int getWarning() {
            return 0;
        }

        @Override
        public Integer getTransactionIsolationLevel() {
            return 0;
        }

        @Override
        public void setTransactionIsolationLevel(Integer transactionIsolationLevel) {

        }

        @Override
        public Prepare getPrepareCacheCmd(String sql, BasePreparedStatement preparedStatement) {
            return null;
        }

        @Override
        public Prepare putPrepareCacheCmd(String sql, Prepare result, BasePreparedStatement preparedStatement) {
            return null;
        }

        @Override
        public long getThreadId() {
            return 0;
        }

        @Override
        public int getStateFlag() {
            return 0;
        }

        @Override
        public int getServerStatus() {
            return 0;
        }

        @Override
        public byte[] getSeed() {
            return null;
        }

        @Override
        public boolean hasServerCapability(long l) {
            return false;
        }

        @Override
        public boolean hasClientCapability(long l) {
            return false;
        }

        @Override
        public ExceptionFactory getExceptionFactory() {
            return null;
        }

        @Override
        public String getDatabase() {
            return null;
        }

        @Override
        public boolean canSkipMeta() {
            return false;
        }

        @Override
        public Function<ReadableByteBuf, ColumnDecoder> getColumnDecoderFunction() {
            return null;
        }

        @Override
        public void addStateFlag(int state) {
        }

        @Override
        public void setCharset(String s) {

        }

        @Override
        public TimeZone getConnectionTimeZone() {
            return null;
        }

        @Override
        public void setConnectionTimeZone(TimeZone connectionTimeZone) {

        }

        @Override
        public Calendar getDefaultCalendar() {
            return null;
        }

        @Override
        public void setThreadId(long l) {

        }

        @Override
        public void setTreadsConnected(long l) {

        }

        @Override
        public String getCharset() {
            return null;
        }

        @Override
        public boolean permitPipeline() {
            return false;
        }

        @Override
        public void setAutoIncrement(long l) {

        }

        @Override
        public void setRedirectUrl(String redirectUrl) {

        }

        @Override
        public String getRedirectUrl() {
            return "";
        }

        @Override
        public Long getAutoIncrement() {
            return null;
        }
    }
}
