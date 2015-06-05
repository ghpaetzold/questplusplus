echo $1 | ngram -order 4 -lm lmfile -ppl - -debug -no-eos -no-sos > tmpfile

./create-ngram-feature.sh tmpfile outputfile
