# CMAKE generated file: DO NOT EDIT!
# Generated by "MinGW Makefiles" Generator, CMake Version 3.29

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

SHELL = cmd.exe

# The CMake executable.
CMAKE_COMMAND = "C:\Program Files\JetBrains\CLion 2023.3.4\bin\cmake\win\x64\bin\cmake.exe"

# The command to remove a file.
RM = "C:\Program Files\JetBrains\CLion 2023.3.4\bin\cmake\win\x64\bin\cmake.exe" -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/homework4_public.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include CMakeFiles/homework4_public.dir/compiler_depend.make

# Include the progress variables for this target.
include CMakeFiles/homework4_public.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/homework4_public.dir/flags.make

CMakeFiles/homework4_public.dir/client.c.obj: CMakeFiles/homework4_public.dir/flags.make
CMakeFiles/homework4_public.dir/client.c.obj: C:/Users/Matei/Documents/UPB/Anul2/Sem2/PCOM/tema4/homework4-public/client.c
CMakeFiles/homework4_public.dir/client.c.obj: CMakeFiles/homework4_public.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\cmake-build-debug\CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building C object CMakeFiles/homework4_public.dir/client.c.obj"
	C:\MinGW\bin\gcc.exe $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -MD -MT CMakeFiles/homework4_public.dir/client.c.obj -MF CMakeFiles\homework4_public.dir\client.c.obj.d -o CMakeFiles\homework4_public.dir\client.c.obj -c C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\client.c

CMakeFiles/homework4_public.dir/client.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing C source to CMakeFiles/homework4_public.dir/client.c.i"
	C:\MinGW\bin\gcc.exe $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\client.c > CMakeFiles\homework4_public.dir\client.c.i

CMakeFiles/homework4_public.dir/client.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling C source to assembly CMakeFiles/homework4_public.dir/client.c.s"
	C:\MinGW\bin\gcc.exe $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\client.c -o CMakeFiles\homework4_public.dir\client.c.s

# Object files for target homework4_public
homework4_public_OBJECTS = \
"CMakeFiles/homework4_public.dir/client.c.obj"

# External object files for target homework4_public
homework4_public_EXTERNAL_OBJECTS =

homework4_public.exe: CMakeFiles/homework4_public.dir/client.c.obj
homework4_public.exe: CMakeFiles/homework4_public.dir/build.make
homework4_public.exe: CMakeFiles/homework4_public.dir/linkLibs.rsp
homework4_public.exe: CMakeFiles/homework4_public.dir/objects1.rsp
homework4_public.exe: CMakeFiles/homework4_public.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --bold --progress-dir=C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\cmake-build-debug\CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking C executable homework4_public.exe"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles\homework4_public.dir\link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/homework4_public.dir/build: homework4_public.exe
.PHONY : CMakeFiles/homework4_public.dir/build

CMakeFiles/homework4_public.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles\homework4_public.dir\cmake_clean.cmake
.PHONY : CMakeFiles/homework4_public.dir/clean

CMakeFiles/homework4_public.dir/depend:
	$(CMAKE_COMMAND) -E cmake_depends "MinGW Makefiles" C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\cmake-build-debug C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\cmake-build-debug C:\Users\Matei\Documents\UPB\Anul2\Sem2\PCOM\tema4\homework4-public\cmake-build-debug\CMakeFiles\homework4_public.dir\DependInfo.cmake "--color=$(COLOR)"
.PHONY : CMakeFiles/homework4_public.dir/depend
