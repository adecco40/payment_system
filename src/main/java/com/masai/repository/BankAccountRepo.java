/**
 * 
 */
package com.masai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.masai.model.BankAccount;

/**
 * @author mirkhamidov
 *
 */

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, String> {

}
