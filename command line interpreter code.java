import java.util.*;
import java.io.File;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.io.*;
class Parser
{
    String commandName;
    String[] args;
    public boolean parse(String input)
    {
        String[] parts = input.split(" ");
        if (parts.length > 0)
        {
            commandName = parts[0];
            args = Arrays.copyOfRange(parts, 1, parts.length);
            return true;
        }
        else
        {
            return false;
        }
    }
    public String getCommandName()
    {
        return commandName;
    }
    public String[] getArgs()
    {
        return args;
    }
}

public class Terminal
{
    Parser parser;
    private Path currentDirectory;
    public static List<String> commandHistory = new ArrayList<>();
    public Terminal()
    {
        currentDirectory = Paths.get(System.getProperty("user.home"));
    }
    public String pwd()
    {
        return currentDirectory.toString();
    }
    public void echo(String[] arg)
    {
        String Arg = String.join(" ", arg);
        System.out.println(Arg);
    }
    public void ls() {
        try {
            DirectoryStream<Path> files = Files.newDirectoryStream(currentDirectory);
            for (Path file : files)
            {
                System.out.println(file.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Error listing files: " + e.getMessage());
        }
    }
    public void lsr() {
        try {
            List<Path> files = Files.list(currentDirectory)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
            for (Path file : files) {
                System.out.println(file.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Error listing files: " + e.getMessage());
        }
    }
    public void cat(String filename) {
        try {
            Path file = currentDirectory.resolve(filename);
            if (Files.exists(file) && Files.isRegularFile(file))
            {
                List<String> lines = Files.readAllLines(file);
                for (String line : lines) {
                    System.out.println(line);
                }
            }
            else
            {
                System.out.println("File cannot be printed: " + filename);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    public void cat(String filename1, String filename2) {
        try {
            Path file1 = currentDirectory.resolve(filename1);
            Path file2 = currentDirectory.resolve(filename2);
            if (Files.exists(file1) && Files.exists(file2) && Files.isRegularFile(file1) && Files.isRegularFile(file2))
            {
                List<String> lines1 = Files.readAllLines(file1);
                List<String> lines2 = Files.readAllLines(file2);
                for (String line : lines1)
                {
                    System.out.println(line);
                }
                for (String line : lines2)
                {
                    System.out.println(line);
                }
            }
            else
            {
                System.out.println("One or both files cannot be printed.");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    public void mkdir(String... directoryNames) {
        for (String dirName : directoryNames) {
            File newDirectory;
            if (Paths.get(dirName).isAbsolute())
            {
                newDirectory = new File(dirName);
            }
            else
            {
                newDirectory = new File(currentDirectory.toFile(), dirName);
            }
            if (!newDirectory.exists())
            {
                newDirectory.mkdir();
                System.out.println("Directory created successfully: " + newDirectory.getAbsolutePath());
            }
            else
            {
                System.out.println("Directory already exists: " + newDirectory.getAbsolutePath());
            }
        }
    }
    public void rmdir(String dirName) {

        if (dirName.equals("*")) {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(currentDirectory)) {
                for (Path dir : dirStream) {
                    try {
                        if (Files.isDirectory(dir) && isEmptyDirectory(dir)) {
                            Files.delete(dir);
                            System.out.println("Directory removed successfully: " + dir.getFileName());
                        }
                    } catch (AccessDeniedException e) {
                        System.out.println("Access denied for directory: " + dir.getFileName());
                    } catch (IOException e) {
                        System.out.println("Failed to remove directory: " + dir.getFileName() + " - " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("Failed " + e.getMessage());
            }
        }  else {
            Path targetDirectory = currentDirectory.resolve(dirName);
                try {
                    Files.delete(targetDirectory);
                    System.out.println("Directory removed successfully: " + targetDirectory.getFileName());
                } catch (NoSuchFileException e) {
                    System.out.println("Invalid Directory Name: " + targetDirectory.getFileName());
                } catch (DirectoryNotEmptyException e) {
                    System.out.println("Directory is not empty: " + targetDirectory.getFileName());
                } catch (IOException e) {
                    System.out.println("Failed to remove directory: " + e.getMessage());
                }
        }
    }
    private static boolean isEmptyDirectory(Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
    public void touch(String fileName) {
            Path newFile = currentDirectory.resolve(fileName);
            try {
                Files.createFile(newFile);
                System.out.println("File created successfully: " + newFile.toAbsolutePath());
            } catch (FileAlreadyExistsException e) {
                System.out.println("File already exists: " + newFile.toAbsolutePath());
            } catch (IOException e) {
                System.out.println("Failed to create file: " + newFile.toAbsolutePath());
            }
        }
    public void rm(String Fname) throws IOException {
        Path fileP = currentDirectory.resolve(Fname);
        if (Files.exists(fileP) && Files.isRegularFile(fileP))
        {
            Files.delete(fileP);
            System.out.println("File is deleted");
        }
        else
        {
            System.out.println("File does not exist or can't be deleted");
        }
    }
    public void cp(File f1, File f2)
    {
        Path F1=currentDirectory.resolve(f1.toString());
        Path F2=currentDirectory.resolve(f2.toString());
        File file1=new File(F1.toString());
        File file2=new File(F2.toString());
        try
        {
            FileInputStream Fin = new FileInputStream(file1);
            FileOutputStream Fout = new FileOutputStream(file2);
            int x;
            while ((x = Fin.read()) != -1)
            {
                Fout.write(x);
            }
            System.out.println("File copied");
            Fin.close();
            Fout.close();
        }
        catch (IOException e)
        {
            System.out.println("Error copying file:" + e.getMessage());
        }
    }
    public static void history()
    {
        for (int i = 0; i < commandHistory.size(); i++)
        {
            System.out.println((i + 1) + ". " + commandHistory.get(i));
        }
    }
    public void cd()
    {
        currentDirectory = Paths.get(System.getProperty("user.home"));
    }
    public void cd(String directoryName) {
        if (directoryName.equals("..")) {
            Path parentDirectory = currentDirectory.getParent();
            if (parentDirectory != null) {
                currentDirectory = parentDirectory;
            } else {
                System.out.println("Already in the root directory.");
            }
        } else {
            try {
                Path newDir;
                if (Paths.get(directoryName).isAbsolute()) {
                    newDir = Paths.get(directoryName);
                } else {
                    newDir = currentDirectory.resolve(directoryName);
                }
                if (Files.isDirectory(newDir)) {
                    currentDirectory = newDir;
                } else {
                    System.out.println("Not a valid directory: " + directoryName);
                }
            } catch (InvalidPathException e) {
                System.out.println("Invalid directory name: " + directoryName);
            }
        }
    }
    public void wc(String filename) {
        try {
            Path file;
            if (Paths.get(filename).isAbsolute()) {
                file = Paths.get(filename);
            } else {
                file = currentDirectory.resolve(filename);
            }
            if (Files.exists(file) && Files.isRegularFile(file)) {
                int lineCount = 0;
                int wordCount = 0;
                int characterCount = 0;
                try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lineCount++;
                        characterCount += line.length();
                        String[] words = line.trim().split("\\s+");
                        wordCount += words.length;
                    }
                }
                System.out.println(lineCount + " " + wordCount + " " + characterCount + " " + filename);
            } else {
                System.out.println("File cannot be counted: " + filename);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    public void chooseCommandAction() throws IOException {
        switch (parser.getCommandName()) {
            case "pwd":
                if (parser.getArgs().length > 0) {
                    System.out.println("pwd does not take any arguments.");
                } else {
                    System.out.println(pwd());
                }
                break;
            case "ls":
                if (parser.getArgs().length > 0) {
                    if (parser.getArgs()[0].equals("-r")) {
                        if (parser.getArgs().length > 1) {
                            System.out.println("ls -r does not take any arguments.");
                        } else {
                            lsr();
                        }
                    } else {
                        System.out.println("ls does not take any arguments.");
                    }
                } else {
                    ls();
                }
                break;
            case "cat":
                if (parser.getArgs().length == 1) {
                    cat(parser.getArgs()[0]);
                } else if (parser.getArgs().length == 2) {
                    cat(parser.getArgs()[0], parser.getArgs()[1]);
                } else {
                    System.out.println("cat command requires 1 or 2 arguments.");
                }
                break;
            case "cd":
                if (parser.getArgs().length == 0) {
                    cd();
                } else if (parser.getArgs().length == 1) {
                    cd(parser.getArgs()[0]);
                } else {
                    System.out.println("cd command requires 1 argument.");
                }
                break;
            case "mkdir":
                if (parser.getArgs().length >= 1) {
                    mkdir(parser.getArgs());
                } else {
                    System.out.println("mkdir command requires at least 1 argument.");
                }
                break;
            case "rmdir":
                if (parser.getArgs().length == 1) {
                    rmdir(parser.getArgs()[0]);
                }
                else {
                    System.out.println("rmdir takes one argument");
                }
                break;
            case "touch":
                if (parser.getArgs().length >= 1) {
                    touch(parser.getArgs()[0]);
                } else {
                    System.out.println("touch requires at least 1 argument");
                }
                break;
            case "history":
                if (parser.getArgs().length > 0) {
                    System.out.println("history does not take any arguments.");
                } else {
                    history();
                }
                break;
            case "echo":
                if (parser.getArgs().length > 0) {
                    echo(parser.getArgs());
                } else {
                    System.out.println("echo requires an argument.");
                }
                break;
            case "rm": {
                if (parser.getArgs().length > 0) {
                    String file = String.join(" ", parser.getArgs());
                    rm(file);
                }
                else { System.out.println("rm requires an argument.");}
                break;
            }
            case "cp":
            {
                if (parser.getArgs().length == 2 )
                {
                    cp(new File(parser.getArgs()[0]), new File(parser.getArgs()[1]));
                }
                else
                {
                    System.out.println("Error: Command not found or invalid parameters are entered!");
                }
                break;
            }
            case "wc":
            {
                if (parser.getArgs().length == 1) {
                    Path filePath = Paths.get(parser.getArgs()[0]);
                    wc(filePath.toString());
                } else {
                    System.out.println("wc command requires 1 argument.");
                }
                break;
            }
            default:
                System.out.println("Command not found: " + parser.getCommandName());
                break;
        }
    }

    public static void main(String[] args) throws IOException
    {
        Scanner cin = new Scanner(System.in);
        System.out.println("Choose the desired command: ");
        System.out.println("echo");
        System.out.println("pwd");
        System.out.println("cd");
        System.out.println("ls");
        System.out.println("ls -r");
        System.out.println("mkdir");
        System.out.println("rmdir");
        System.out.println("touch");
        System.out.println("cp");
        System.out.println("rm");
        System.out.println("cat");
        System.out.println("wc");
        System.out.println("history");
        System.out.println("Enter exit to terminate the CLI");
        System.out.print(">");
        String input = cin.nextLine();
        Terminal t = new Terminal();
        t.parser = new Parser();
        t.parser.parse(input);
        commandHistory.add(input);
        while (!t.parser.getCommandName().equals("exit"))
        {
            t.chooseCommandAction();
            System.out.print(">");
            input = cin.nextLine();
            t.parser.parse(input);
            commandHistory.add(input);
        }
    }
}