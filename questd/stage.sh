#!/bin/bash
./activator clean compile stage

service questd restart

