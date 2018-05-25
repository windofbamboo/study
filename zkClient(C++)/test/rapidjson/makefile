#COMAKE2 edit-mode: -*- Makefile -*-
####################64Bit Mode####################
ifeq ($(shell uname -m),x86_64)
CC=gcc
CXX=g++
CXXFLAGS=-g -pipe -W -Wall -fPIC
CFLAGS=-g -pipe -W -Wall -fPIC
CPPFLAGS=-D_GNU_SOURCE -D__STDC_LIMIT_MACROS

INCPATH=-I. \
				-I/usr/include \
				-I/home/roam/include/rapidjson

rapidJsonTest:rapidJsonTest.o
	$(CXX) rapidJsonTest.o -Xlinker "-(" -lrt -Xlinker "-)" -o rapidJsonTest

rapidJsonTest.o:rapidJsonTest.cc
	$(CXX) -c $(INCPATH) $(CPPFLAGS) $(CXXFLAGS) -g -o rapidJsonTest.o rapidJsonTest.cc

endif #ifeq ($(shell uname -m),x86_64)