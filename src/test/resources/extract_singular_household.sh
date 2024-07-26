#!/bin/bash
# This is a ChatGPT generated bash script to create a subset of household / car / person / activity.

# Check if the input folder is provided as an argument
if [ -z "$1" ]; then
  echo "Usage: $0 <folder_path>"
  exit 1
fi

# Define the invariant file name
HOUSEHOLD_FILE="household.csv"
CAR_FILE="car.csv"
PERSON_FILE="person.csv"
ACTIVITY_FILE="activity.csv"

OUTPUT_FILE=""
# Construct the full path to the file
FILE_PATH="$1/$HOUSEHOLD_FILE"

OUTPUT_FILE="$HOUSEHOLD_FILE"
# Check if the file exists
if [ ! -f "$FILE_PATH" ]; then
  echo "File $FILE_PATH does not exist."
  exit 1
fi

# Extract the header line and lines starting with "982406"
awk 'NR==1 || /^"982406"/' "$FILE_PATH" >> "$OUTPUT_FILE"

# Construct the full path to the file
FILE_PATH="$1/$CAR_FILE"
OUTPUT_FILE="$CAR_FILE"
# Check if the file exists
if [ ! -f "$FILE_PATH" ]; then
  echo "File $FILE_PATH does not exist."
  exit 1
fi

# Extract the header line and lines starting with "982406"
awk 'NR==1 || /^"982406"/' "$FILE_PATH" >> "$OUTPUT_FILE"


# Construct the full path to the file
FILE_PATH="$1/$PERSON_FILE"
OUTPUT_FILE="$PERSON_FILE"
# Check if the file exists
if [ ! -f "$FILE_PATH" ]; then
  echo "File $FILE_PATH does not exist."
  exit 1
fi

# Extract the header line and lines starting with "982406"
awk -F';' 'NR==1 || $3 ~ /982406/' "$FILE_PATH" >> "$OUTPUT_FILE"

# Construct the full path to the file
FILE_PATH="$1/$PERSON_FILE"
OUTPUT_FILE="$PERSON_FILE"
# Check if the file exists
if [ ! -f "$FILE_PATH" ]; then
  echo "File $FILE_PATH does not exist."
  exit 1
fi

# Extract the header line and lines starting with "982406"
awk -F';' 'NR==1 || $3 ~ /982406/' "$FILE_PATH" >> "$OUTPUT_FILE"

FILE_PATH="$1/$ACTIVITY_FILE"
OUTPUT_FILE="$ACTIVITY_FILE"
# Check if the file exists
if [ ! -f "$FILE_PATH" ]; then
  echo "File $FILE_PATH does not exist."
  exit 1
fi

# Extract the header line and lines starting with "982406"
awk -F';' 'NR==1 || /^"175315(6|7|8|9)"/' "$FILE_PATH" >> "$OUTPUT_FILE"
