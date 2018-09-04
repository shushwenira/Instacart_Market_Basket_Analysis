import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.sql.SparkSession

object Instacart_Market_Basket_Analysis {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("Need three parameters ")
    }
    val spark = SparkSession
      .builder()
      .appName("Instacart_Market_Basket_Analysis")
      .getOrCreate()

    val sc = spark.sparkContext

    // Load data-set
    val op = spark.read.option("header","true").option("inferSchema","true").csv(args(0))
    val products = spark.read.option("header","true").option("inferSchema","true").csv(args(1))

    //DF to RDD - as the FPGrowth function needs a RDD
    val groupedRDD = op.select("order_id","product_id").rdd.map(x => (x(0), x(1))).groupByKey()
    val valueArray = groupedRDD.mapValues(_.toArray).collectAsMap().values.toArray

    //Creating transactions
    val transactions = sc.parallelize(valueArray)


    val fpg = new FPGrowth().setMinSupport(0.01)

    //Creating FPGrowth Model
    val model = fpg.run(transactions)

    var output = ""
    output += "Top 10 products: \n\n"
    output += "[Products], Frequency \n\n"

    //Finding Frequent Itemsets
    val FP = model.freqItemsets.map(x =>(x.items, x.freq)).sortBy(-_._2).take(10)
    FP.map{ x => x._1.foreach( product_id => output+= products.filter(products("product_id") === product_id).select("product_name").take(1)(0).toString())
      output+= ", " + x._2+"\n"
    }

    output += "\n\nTop 10 Association Rules: \n\n"
    output += "[Antecedent] [Consequent], Confidence \n\n"
    val minConfidence = 0.2
    val AssoRules = model.generateAssociationRules(minConfidence)
      .map(rule =>(rule.antecedent, rule.consequent, rule.confidence)).sortBy(-_._3).take(10)

    AssoRules.map{ x => x._1.foreach( product_id => output+= products.filter(products("product_id") === product_id).select("product_name").take(1)(0).toString())
      x._2.foreach( product_id => output+= products.filter(products("product_id") === product_id).select("product_name").take(1)(0).toString())
      output+= ", " + x._3+"\n"
    }
    sc.parallelize(List(output)).saveAsTextFile(args(2))
    sc.stop()

  }
}