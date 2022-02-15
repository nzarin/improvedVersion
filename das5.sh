#!/bin/bash
#SBATCH --job-name=example1
#SBATCH --output=output.txt
#SBATCH --time=15:00

make all
