#OVERVIEW

Page rank implementation on the authors and venues graph generated from the DBLP dataset using SPARK.

##Description

- [DBLP dataset]() (represented in XML format) describes the publications, authors and the venues of the publication.
- This file is hosted on HDFS and logical splits of each publication is done using the custom implementation of [XMLInputFormat]().
- RDD is constructed using this file on HDFS.
- The Following operations are performed on each logical input/entry of the dataset
    - Authors and venues are extracted.
    - Node links are generated between the co-authors and the venues (bi-directional graph is considered).
    - Every link in the graph is given am initial probability (Initial page rank).
    - These links are cached since page rank runs on the basis of these links and every worker/task needs access to it.
- Page rank is applied iteratively over the nodes/links in the graph. The damping/teleportation factor is considered to be 0.15.
- After the algorithm stabilizes the page rank values, the ranked nodes are sorted based on the page rank values.
- The authors and venues are filtered out accordingly based on their ranks.

The following is an example output for the UIC authors in the DBLP dataset. Ranked based on their page rank values :

```$xslt
    (Philip S. Yu,72.17358923786207)
    
    (Bing Liu,39.06762535082672)
    
    (Ouri Wolfson,28.496776227313035)
    
    (Isabel F. Cruz,28.395424558956968)
    
    (Barbara Di Eugenio,20.618331944971104)
    
    (A. Prasad Sistla,20.021441901636937)
    
    (Ajay D. Kshemkalyani,19.832975812113258)
    
    (Bhaskar DasGupta,19.44857424762532)
    
    (Robert H. Sloan,16.733228284339656)
    
    (Lenore D. Zuck,15.596224206984767)
    
    (Xinhua Zhang,14.843940965618545)
    
    (Mark Grechanik,14.731149359854117)
    
    (Daniel J. Bernstein,12.705867944366114)
    
    (Cornelia Caragea,12.6482246855889)
    
    (Peter C. Nelson,10.89272817193666)
    
    (Brian D. Ziebart,10.650371634945353)
    
    (Piotr J. Gmytrasiewicz,10.12146523274226)
    
    (Andrew E. Johnson,9.976089150292744)
    
    (G. Elisabeta Marai,9.50495677144365)
    
    (Jon A. Solworth,9.478505266716834)
    
    (Anastasios Sidiropoulos,8.750119189329835)
    
    (V. N. Venkatakrishnan,8.73235485203786)
    
    (Chris Kanich,8.513713406828218)
    
    (Ian A. Kash,8.03110579252562)
    
    (Tanya Y. Berger-Wolf,7.917603762109459)
    
    (Luc Renambot,7.84879809518071)
    
    (Ugo Buy,7.747956006028061)
    
    (Robert V. Kenyon,7.410715549761395)
    
    (Jakob Eriksson,6.003109936783798)
    
    (Iasonas Polakis,5.7524944190635985)
    
    (Ugo A. Buy,4.983539196984795)
    
    (John Lillis,4.979847093828561)
    
    (Xiaorui Sun,4.720059895596486)
    
    (Mitchell D. Theys,4.504435148594043)
    
    (Elena Zheleva,4.268325084810485)
    
    (Brent Stephens,3.7633315517435775)
    
    (William Mansky,3.6247534020590932)
    
    (Xingbo Wu,3.1140512282426602)
    
    (Debaleena Chattopadhyay,3.030810730021326)
    
    (Bhaskar Dasgupta,3.0144042043348804)
    
    (Balajee Vamanan,2.706039520901)
    
    (Joseph Hummel,2.570502229898204)
    
    (Natalie Parde,2.429882622999021)
    
    (Emanuelle Burton,2.232892318164791)
    
    (Jason Polakis,1.8960665762929272)
    
    (Dale Reed,1.864310213929018)
    
    (Nasim Mobasheri,1.286177926745608)
    
    (Natalie Paige Parde,1.0)
    
    (Evan McCarty,1.0)
    
    (Patrick Troy,0.9763448991251961)
    
    (John T. Bell,0.848909105338623)
    
    (Gonzalo A. Bello,0.7188920286435473)
    
    (Prasad Sistla,0.6571224614324404)
    
    (Ouri E. Wolfson,0.4993680310193368)
    
    (Georgeta Elisabeta Marai,0.4918656726866626)
```
    
    
   
And the Venues ranked 

```$xslt
(CoRR,7.283664532969762)

(IJCAI,2.8915321964265495)

(AAAI,2.6691634919771645)

(SIGCSE,2.2561828226006035)

(ACM Conference on Computer and Communications Security,1.9034485726778618)

(ASONAM,1.882170704298203)

(Inf. Process. Lett.,1.8467006238980814)

(SODA,1.839465802997625)

(CollaborateCom,1.730722037091281)

(WWW,1.7196391848658394)

(IEEE Trans. Knowl. Data Eng.,1.7156789486724864)

(SIGMOD Conference,1.6921312263189152)

(IEEE Trans. Parallel Distrib. Syst.,1.6516080153109864)

(J. Parallel Distrib. Comput.,1.6504236768509981)

(NIPS,1.6194651975382972)

(Theor. Comput. Sci.,1.6130057198222363)

(J. Comput. Syst. Sci.,1.6056537767001755)

(COLT,1.5857082534384472)

(INFOCOM,1.5068868882251152)

(IEEE Internet Computing,1.476026270184349)

(SIGSPATIAL/GIS,1.4609961743649853)

(IEEE Trans. Software Eng.,1.4583996611933194)

(SSDBM,1.4229981330471677)

(ICDE,1.4061534796843165)

(CIKM,1.404684814788919)

(USENIX Security Symposium,1.3980551422263565)

(FLAIRS Conference,1.3962638900884665)

(ACM Comput. Surv.,1.3887626162614253)

(IEEE Symposium on Security and Privacy,1.365905866706006)

(STOC,1.3097520866027694)

(VR,1.2869894243623956)

(Advances in Computers,1.2358346967545195)

(Knowl.-Based Syst.,1.223579497648112)

(ICSC,1.2107508825650262)

(COLING,1.2072275098014131)

(Applied Artificial Intelligence,1.20422241970802)

(IEEE Trans. Systems, Man, and Cybernetics, Part A,1.200774079926246)

(PerCom Workshops,1.197981271541788)

(Encyclopedia of Database Systems (2nd ed.),1.1971447808810018)

(BMC Bioinformatics,1.190348807813393)

(TWEB,1.1902795622523568)

(EMNLP,1.1888521044284945)

(ICDCS,1.1874988178262844)

(MDM,1.180446347272571)

(GIS,1.180446347272571)

(COMPSAC,1.1786678451312398)

(SIGMOD Record,1.165995618460514)

(VLDB J.,1.165995618460514)

(EDBT,1.165995618460514)

(Artif. Intell.,1.1634320850227833)

(PVLDB,1.159815400511627)
.
.
.
.
.
``` 
    

## Steps to Run 

Build the jar using

````
sbt clean assembly
````

This will run all the test cases and if they pass (which they will) builds a fat jat.

Run the following command to run the spark job in the `local mode` 

```
spark-submit --class PageRank \
--master local --deploy-mode client --executor-memory 1g \
--name sampleRun --conf "spark.app.id=sampleRun" \
Spark-PageRank-assembly-0.1.0.jar hdfs://192.168.151.156:8020/user/cloudera/input/dblp.xml hdfs://192.168.151.156:8020/user/cloudera/rankedAuthors hdfs://192.168.151.156:8020/user/cloudera/rankedVenues
```
where `hdfs://192.168.151.156:8020/user/cloudera/input/dblp.xml` is the input path containing the dblp file. Replace with your HDFS path.

and `hdfs://192.168.151.156:8020/user/cloudera/rankedAuthors` `hdfs://192.168.151.156:8020/user/cloudera/rankedVenues`  are output paths for the authors and venues respectively.


Command to run on the amazon EMR :

```$xslt
 spark-submit --class PageRank s3://mohammedsiddiq.cs.uic/pageRankJar/Spark-PageRank-assembly-0.1.0.jar s3://mohammedsiddiq.cs.uic/dblp.xml s3://mohammedsiddiq.cs.uic/latestauthorsranked s3://mohammedsiddiq.cs.uic/latestvenuesranked
```

Please refer this short video for summary of the running project on EMR.