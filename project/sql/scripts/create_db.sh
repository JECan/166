#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -p 1521 vgarc018db < $DIR/../src/create_tables.sql
psql -p 1521 vgarc018db < $DIR/../src/create_indexes.sql
psql -p 1521 vgarc018db < $DIR/../src/load_data.sql
