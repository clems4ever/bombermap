#include <stdlib.h>
#include <limits>
#include <iostream>

typedef std::numeric_limits< double > dbl;
int main()
{
   double parislat=2.3228797;
   double parislong=48.8870407;
   srand(time(NULL));
   double latitude = parislat + ((double) rand() / (RAND_MAX))/1000;
   double longitude = parislong + ((double) rand() / (RAND_MAX))/1000;
   std::cout.precision(10);
   std::cout << std::fixed << "geo fix " << latitude << " " << longitude << std::endl;
   return 0; 
}
