/*
 * Copyright (c) 2020, CASTOR Software Research Centre (www.castor.kth.se)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package se.kth.depclean.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public final class MavenInvoker
{
   private MavenInvoker()
   {
   }

   /**
    * Creates a native process to execute a custom command. This method is used to invoke maven plugins directly.
    *
    * @param cmd The command to be executed.
    * @return The console output.
    * @throws IOException In case of IO issues.
    */
   public static String[] runCommand(String cmd) throws IOException
   {
      ArrayList list = new ArrayList();
      Process process = Runtime.getRuntime().exec(cmd);
      InputStream inputStream = process.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
      String s; // Temporary String variable
      while ((s = br.readLine()) != null)
         list.add(s);
      try {
         process.waitFor();
      } catch (InterruptedException e) {
         System.err.println("Process was interrupted");
      }
      br.close();
      return (String[]) list.toArray(new String[0]);
   }

   /**
    * This method invokes Maven to execute a given goal programmatically instead of running a command directly as
    * in {@link #runCommand(String)}.
    *
    * @param mvnHome Location of maven installation.
    * @param pomPath Path to the pom of the project.
    * @param mvnGoal The maven goal to execute.
    * @return The exit code from the Maven invocation.
    * @throws MavenInvocationException In case of any issue invoking maven.
    */
   public static int invokeMaven(String mvnHome, String pomPath, String mvnGoal) throws MavenInvocationException
   {
      InvocationRequest request = new DefaultInvocationRequest();
      request.setPomFile(new File(pomPath));
      request.setGoals(Collections.singletonList(mvnGoal));
      Invoker invoker = new DefaultInvoker();
      invoker.setMavenHome(new File(mvnHome));
      InvocationResult result = invoker.execute(request);
      return result.getExitCode();
   }
}