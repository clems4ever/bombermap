#include <iostream>
#include <stdio.h>

unsigned int *buffer;
void readfile() {
    FILE *f = fopen("image.bmp", "rb");
    buffer = new unsigned int[64*64];
    fseek(f, 54, SEEK_SET);
    fread(buffer, 64*64*4, 1, f);
    fclose(f);
}

unsigned int getpixel(int x, int y) {
    //assuming your x/y starts from top left, like I usually do
    return buffer[y * 64 + x];
}

int main () 
{
	readfile();
	for (int i = 0; i < 64 ; ++i)
	{
		for (int j = 0; j < 64; ++j)
		{
			std::cout << getpixel(i, j) << std::endl;
		}	
	}
	return 0;
}

