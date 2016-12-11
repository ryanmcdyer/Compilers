
	echo ---------compiling jjt Files---------
	echo.
	call jjtree BasicL.jjt
	echo.
	echo ---------compiling jj Files---------
	echo.
	call javacc BasicL.jj
	echo.
	echo ---------compiling java Files---------
	echo.
	call javac *.java
	echo.
	echo ---------running test---------
	echo.
	call java BasicL sum_primes.bl
