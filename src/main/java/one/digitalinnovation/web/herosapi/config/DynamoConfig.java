package one.digitalinnovation.web.herosapi.config;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import one.digitalinnovation.web.herosapi.model.Heroes;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
@EnableDynamoDBRepositories
public class DynamoConfig {

    @Value("${aws.accesskey}")
    private String amazonAwsAccessKey;

    @Value("${aws.secretkey}")
    private String amazonAwsSecretKey;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        final Regions saoPaulo = Regions.SA_EAST_1;  // South America(São Paulo)
        final AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(saoPaulo)
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(amazonAwsAccessKey,amazonAwsSecretKey)
                        )
                ).build();

        return dynamoDB;
    }

    @PostConstruct
    private void createHeroesTableIfNotExists(){
        final AmazonDynamoDB dynamoDB = amazonDynamoDB();
        try {
            //check if table exists, if it doesn't an exception will be raised
            DescribeTableResult describeTable = dynamoDB.describeTable("Heroes_Table");
            System.out.println("\nHeroes_table loaded");
            System.out.println(describeTable.getTable().toString() + "\n");

        } catch (ResourceNotFoundException e) {
            //since table doesn't exists we create it
            System.out.println("\nCreating Heroes_Table");
            final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDB);
            final CreateTableRequest request =
                    dynamoDBMapper.generateCreateTableRequest(Heroes.class);
            request.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

            try {
                final CreateTableResult result = dynamoDB.createTable(request);

                System.out.println("Status : " +  result.getSdkHttpMetadata().getHttpStatusCode());
                System.out.println("Table Name : " +  result.getTableDescription().getTableName());
                System.out.println("Heroes_Table created\n");

            } catch (AmazonServiceException err) {
                System.out.println(err.getErrorMessage());
            }
        }
    }

}
