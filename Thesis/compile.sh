pdflatex -draftmode main.tex
biber main
pdflatex -draftmode main.tex
pdflatex -draftmode main.tex
pdflatex main.tex
open main.pdf

# rm -f main.aux
# rm -f main.bbl
# rm -f main.bcf
# rm -f main.blg
# rm -f main.lof
# rm -f main.log
# rm -f main.out
# rm -f main.run.xml
# rm -f main.toc
# rm -f main.lot
# rm -f main.fls
# rm -f main.fdb_latexmk
