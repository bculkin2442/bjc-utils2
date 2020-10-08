.PHONY: all clean

all: clformat.pdf

clean:
	rm clformat.pdf

clformat.pdf: clformat.ps
	ps2pdf clformat.ps clformat.pdf
	rm clformat.ps

clformat.ps: clformat.ms
	groff -ms -s -t -Tps clformat.ms > clformat.ps

clformat.ms: clformat.tmac
	touch clformat.ms

clformat.tmac:
	touch clformat.tmac

preview: clformat.pdf
	fbpdf clformat.pdf
