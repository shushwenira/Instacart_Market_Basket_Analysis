
Steps to run the Application:

--------------------------------------------------------------------------------------

Build the SBT application, Upload the Jar and the coresponding kaggle csv 
(https://www.kaggle.com/c/instacart-market-basket-analysis) data into a s3 bucket.

Run the Elastic Map Reduce Step with Following configuration:

---------------------------------------------------------------------------------------

Step type: Spark Application
Name: Instacart_Market_Basket_Analysis
Deploy mode: Cluster
Spark-submit options: --class "Instacart_Market_Basket_Analysis"
Application location*: s3://your-bucket/Instacart_Market_Basket_Analysis_2.11-0.1.jar
Arguments: s3://your-bukcet/order_products_train.csv
 	   s3://your-bukcet/products.csv
	   s3://your-bukcet/output-folder
Action on failure: Continue
