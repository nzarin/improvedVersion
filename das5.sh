#!/bin/bash
#SBATCH --job-name=example1
#SBATCH --output=output.txt
#SBATCH --time=10:00
#SBATCH --reservation=edull

echo 'Hi from '$HOSTNAME'!!!'
sleep 20