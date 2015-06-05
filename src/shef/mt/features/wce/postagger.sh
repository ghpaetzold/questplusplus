current=`pwd`
#echo $current
#Go to the postagger script folder
cd $1
#/Users/luongngocquang/Documents/Setups/tree-tagger-MacOSX-3.2-intel/tagger-scripts/
#echo "He is the final competitor ." | ./tree-tagger-english | awk '{ print $2 }' | tr -d '\n' > myfile3

echo $2 | ./tree-tagger-spanish | awk '{ print $2 }' # | tr -d '\n' 
#mv myfile3  $current
cd $current
#echo `pwd`
