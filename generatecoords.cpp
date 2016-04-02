#include <stdlib.h>
#include <limits>
#include <iostream>
#include <string>
#include <sstream>

typedef std::numeric_limits< double > dbl;
int main(int argc, char *argv[])
{
   std::stringstream stream1;
   std::stringstream stream2;
   std::string input1=argv[1];
   std::string input2=argv[2];
   
   double lat, longi;
   stream1 << input1;
   stream1 >> lat;
   stream2 << input2;
   stream2 >> longi;

   std::cout << lat << std::endl;
   std::cout << longi << std::endl;

   double parislat=2.3228797+lat/10000;
   double parislong=48.8870407+longi/10000;
   srand(time(NULL));
   double latitude = parislat + ((double) rand() / (RAND_MAX))/1000;
   double longitude = parislong + ((double) rand() / (RAND_MAX))/1000;
   std::cout.precision(10);
   std::cout << std::fixed << "geo fix " << latitude << " " << longitude << std::endl;
   return 0; 
}
