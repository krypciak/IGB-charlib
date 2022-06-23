IGB charLib is used for generating [**IGB L2 code**](https://github.com/krypciak/IGB-Compiler-L2) that draws chars on the screen.  

Currently using the [**Advanced Pixel-7**](https://www.1001freefonts.com/advanced-pixel-7.font) font.  

Basic usage:
```java
FontToGenerate[] fontsToGenerate = {
			//                          font name           font type    font size   NAME (generates function named ${NAME}drawchar())
			new FontToGenerate(new Font("Advanced Pixel-7", Font.PLAIN, 	14),     "small"),  // generates function named smalldrawchar()
			new FontToGenerate(new Font("Advanced Pixel-7", Font.PLAIN, 	20),     "big"),    // generates function named bigdrawchar()
};

IGB_charlib charlib = new IGB_charlib(fontsToGenerate, 32, 127);

String l2Code = charlib.getL2Code();
me.krypek.utils.Pair<String[], int[]> pair = charlib.getFormatedL2Code();
```


## Dependencies:
- [Utils](https://github.com/krypciak/Utils)  
- [IGB-Compiler-L1](https://github.com/krypciak/IGB-Compiler-L1)  
- [IGB-Compiler-L2](https://github.com/krypciak/IGB-Compiler-L2)  
- [Advanced Pixel-7](https://www.1001freefonts.com/advanced-pixel-7.font) font installed
