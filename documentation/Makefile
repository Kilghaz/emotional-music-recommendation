pdf:
	@echo "building pdf"
	xelatex -synctex=1 -interaction=nonstopmode documentation.tex

bibtex:
	@echo "running bibtex"
	bibtex documentation

rebuild:
	rm -rf *.out *.log *.blg *.bbl *.aux *.synctex.gz *.pdf
	xelatex -synctex=1 -interaction=nonstopmode documentation.tex
	bibtex documentation
	xelatex -synctex=1 -interaction=nonstopmode documentation.tex
	bibtex documentation
	xelatex -synctex=1 -interaction=nonstopmode documentation.tex

clean:
	@echo "cleaning"
	rm -rf *.out *.log *.blg *.bbl *.aux *.synctex.gz *.pdf
