import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import com.mapsa.core.commits.account.*;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.logger.AccountCommitLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AccountManager {

    private List<Account> accounts;
    private List<AccountCommitStatus> commitStatusList;
    private List<AccountCommitResponse> commitResponseList;
    private Queue<AccountCommit> commitQueue;
    private AccountCommitLogger logger;
    private AccountDAL accountDAL;

    public AccountManager() {
        start();
    }

    public void addAccountCommit(AccountCommit commit) {
        commitQueue.add(commit);
    }

    public void start() {
        accountDAL = new AccountDAL();
        logger = new AccountCommitLogger();
        accounts = accountDAL.loadAccounts();
        if(this.accounts == null){
            accounts = new ArrayList<>();
        }
        commitStatusList = logger.loadAllCommitStatus();
        if(this.commitResponseList == null){
            commitResponseList = new ArrayList<>();
        }
    }

    public void process(AccountCommit accountCommit) {
        if (accountCommit instanceof CreateAccountCommit) {
            boolean ok = createAccountCommitConsumer((CreateAccountCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
        if (accountCommit instanceof DeactivateAccountCommit) {
            boolean ok = deactivateAccountCommitConsumer((DeactivateAccountCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
        if (accountCommit instanceof ReactivateAccountCommit) {
            boolean ok = reactivateAccountCommitConsumer((ReactivateAccountCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
        if (accountCommit instanceof WithdrawCommit) {
            boolean ok = withdrawCommitConsumer((WithdrawCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
        if (accountCommit instanceof DepositCommit) {
            boolean ok = depositCommitConsumer((DepositCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
        if (accountCommit instanceof AccountTransactionCommit) {
            boolean ok = accountTransactionCommitConsumer((AccountTransactionCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
        if (accountCommit instanceof BlockAccountCommit) {
            boolean ok = blockAccountCommitConsumer((BlockAccountCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
        if (accountCommit instanceof AccountHistoryCommit) {
            boolean ok = accountHistoryCommitConsumer((AccountHistoryCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
        if (accountCommit instanceof UnblockAccountCommit) {
            boolean ok = unblockAccountCommitConsumer((UnblockAccountCommit) accountCommit);
            if (ok) {
                commitQueue.remove();
            }
        }
    }

    public boolean blockAccountCommitConsumer(BlockAccountCommit commit) {
        try {
            for (AccountCommitStatus status : this.commitStatusList){
                if (status.getCommitId().equals(commit.getCUID())){
                    return true;
                }
            }
            for (Account acc : accounts) {
                if (acc.getAccountId().equals(commit.getAccountId())) {
                    String previousCommitId = acc.getLastCommitId();
                    acc.block(commit.getCUID());
                    accountDAL.saveAccount(acc);
                    AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(), "Done");
                    commitStatusList.add(commitStatus);
                    AccountCommitLog accountCommitLog = new AccountCommitLog(commit, previousCommitId);
                    BlockAccountCommitResponse blockAccountCommitResponse = new BlockAccountCommitResponse(commit, true);
                    commitResponseList.add(blockAccountCommitResponse);
                    AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(blockAccountCommitResponse);
                    logger.saveCommit(accountCommitLog);
                    logger.saveCommitStatus(commitStatus);
                    logger.saveCommitResponse(accountCommitResponseLog);
                    return true;
                }
            }
            AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(), "Failed");
            commitStatusList.add(commitStatus);
            AccountCommitLog accountCommitLog = new AccountCommitLog(commit, null);
            BlockAccountCommitResponse blockAccountCommitResponse = new BlockAccountCommitResponse(commit, true);
            commitResponseList.add(blockAccountCommitResponse);
            AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(blockAccountCommitResponse);
            logger.saveCommit(accountCommitLog);
            logger.saveCommitStatus(commitStatus);
            logger.saveCommitResponse(accountCommitResponseLog);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unblockAccountCommitConsumer(UnblockAccountCommit commit) {
        for (AccountCommitStatus commitstus : commitStatusList) {
            if (commitstus.getCommitId().equals(commit.getCUID())) {
                return true;
            }
        }
        for (Account acc : accounts) {
            if (acc.getAccountId().equals(commit.getAccountId())) {
                AccountCommitLog accountCommitLog = new AccountCommitLog(commit, acc.getLastCommitId());
                logger.saveCommit(accountCommitLog);
                acc.unblock(commit.getCUID());
                accountDAL.saveAccount(acc);
                AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(), "Done");
                commitStatusList.add(commitStatus);
                UnblockAccountCommitResponse unblockAccountCommitResponse = new UnblockAccountCommitResponse(commit, true);
                commitResponseList.add(unblockAccountCommitResponse);
                AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(unblockAccountCommitResponse);
                logger.saveCommitStatus(commitStatus);
                logger.saveCommitResponse(accountCommitResponseLog);
                return true;
            }
        }
        AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(), "Failed");
        commitStatusList.add(commitStatus);
        AccountCommitLog accountCommitLog = new AccountCommitLog(commit, null);
        UnblockAccountCommitResponse unblockAccountCommitResponse = new UnblockAccountCommitResponse(commit, false);
        commitResponseList.add(unblockAccountCommitResponse);
        AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(unblockAccountCommitResponse);
        logger.saveCommit(accountCommitLog);
        logger.saveCommitStatus(commitStatus);
        logger.saveCommitResponse(accountCommitResponseLog);
        return false;

    }

    public boolean reactivateAccountCommitConsumer(ReactivateAccountCommit commit) {
        try {
            String previous;
            for(AccountCommitStatus acc1:commitStatusList) {
                if (acc1.getCommitId().equals(commit.getCUID())) {
                    return true;
                }
            }
            for (Account acc : accounts) {
                if (acc.getAccountId().equals(commit.getAccountId())) {
                    previous=acc.getLastCommitId();
                    acc.activate(commit.getCUID());
                    accountDAL.saveAccount(acc);
                    AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(), "Done");
                    commitStatusList.add(commitStatus);

                    ReactivateAccountCommitResponse reactivateAccountCommitResponse = new ReactivateAccountCommitResponse(commit, true);
                    commitResponseList.add(reactivateAccountCommitResponse);
                    AccountCommitLog accountCommitLog = new AccountCommitLog(commit,previous);
                    AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(reactivateAccountCommitResponse);
                    logger.saveCommit(accountCommitLog);
                    logger.saveCommitStatus(commitStatus);
                    logger.saveCommitResponse(accountCommitResponseLog);
                    return true;
                }
            }
            AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(), "Failed");
            commitStatusList.add(commitStatus);

            ReactivateAccountCommitResponse reactivateAccountCommitResponse = new ReactivateAccountCommitResponse(commit, false);
            commitResponseList.add(reactivateAccountCommitResponse);
            AccountCommitLog accountCommitLog = new AccountCommitLog(commit,null);
            AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(reactivateAccountCommitResponse);
            logger.saveCommit(accountCommitLog);
            logger.saveCommitStatus(commitStatus);
            logger.saveCommitResponse(accountCommitResponseLog);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deactivateAccountCommitConsumer(DeactivateAccountCommit commit) {
        try {
            for (Account acc : accounts) {
                if (acc.getAccountId().equals(commit.getAccountId())) {
                    String tempCommitId = acc.getLastCommitId();
                    acc.deactive(commit.getCUID());
                    accountDAL.saveAccount(acc);
                    AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(), "Done");
                    commitStatusList.add(commitStatus);
                    AccountCommitLog accountCommitLog = new AccountCommitLog(commit, tempCommitId);
                    DeactivateAccountCommitResponse deactivateAccountCommitResponse = new DeactivateAccountCommitResponse(commit, true);
                    commitResponseList.add(deactivateAccountCommitResponse);
                    AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(deactivateAccountCommitResponse);
                    logger.saveCommit(accountCommitLog);
                    logger.saveCommitStatus(commitStatus);
                    logger.saveCommitResponse(accountCommitResponseLog);
                    return true;
                }
            }
            AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(), "Failed");
            commitStatusList.add(commitStatus);
            AccountCommitLog accountCommitLog = new AccountCommitLog(commit, null);
            DeactivateAccountCommitResponse deactivateAccountCommitResponse = new DeactivateAccountCommitResponse(commit, false);
            commitResponseList.add(deactivateAccountCommitResponse);
            AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(deactivateAccountCommitResponse);
            logger.saveCommit(accountCommitLog);
            logger.saveCommitStatus(commitStatus);
            logger.saveCommitResponse(accountCommitResponseLog);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean withdrawCommitConsumer(WithdrawCommit withdrawCommit) {

        String priveios;
        for (Account account : accounts) {
            if (account.getAccountId().equals(withdrawCommit.getAccountId())) {
                if (account.withdraw(withdrawCommit.getAmount(), withdrawCommit.getCUID())) {
                    priveios=account.getLastCommitId();
                    accountDAL.saveAccount(account);
                    AccountCommitLog accountCommitLog = new AccountCommitLog(withdrawCommit, priveios);
                    logger.saveCommit(accountCommitLog);
                    AccountCommitStatus commitStatus = new AccountCommitStatus(withdrawCommit.getCUID(), "done");
                    logger.saveCommitStatus(commitStatus);
                    commitStatusList.add(commitStatus);
                    AccountCommitResponse accountCommitResponse = new WithdrawCommitResponse(withdrawCommit, true);
                    AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountCommitResponse);
                    logger.saveCommitResponse(accountCommitResponseLog);
                    commitResponseList.add(accountCommitResponse);
                    return true;
                }
            }
        }
        AccountCommitStatus commitStatus = new AccountCommitStatus(withdrawCommit.getCUID(), "failed");
        logger.saveCommitStatus(commitStatus);
        commitStatusList.add(commitStatus);
        AccountCommitLog accountCommitLog = new AccountCommitLog(withdrawCommit, null);
        logger.saveCommit(accountCommitLog);
        AccountCommitResponse accountCommitResponse = new WithdrawCommitResponse(withdrawCommit, false);
        AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountCommitResponse);
        logger.saveCommitResponse(accountCommitResponseLog);
        commitResponseList.add(accountCommitResponse);
        return false;
    }

    public boolean depositCommitConsumer(DepositCommit depositCommit) {

        for (Account account : accounts) {
            if (account.getAccountId().equals(depositCommit.getAccountId()) && depositCommit.getCUID()!=account.getLastCommitId()) {
                String perivuesId = account.getLastCommitId();
                if (account.deposit(depositCommit.getAmount(), depositCommit.getCUID())) {
                    accountDAL.saveAccount(account);
                    AccountCommitLog accountCommitLog = new AccountCommitLog(depositCommit, perivuesId);
                    logger.saveCommit(accountCommitLog);
                    AccountCommitStatus commitStatus = new AccountCommitStatus(depositCommit.getCUID(), "done");
                    logger.saveCommitStatus(commitStatus);
                    commitStatusList.add(commitStatus);
                    AccountCommitResponse accountCommitResponse = new DepositCommitResponse(depositCommit, true);
                    AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountCommitResponse);
                    logger.saveCommitResponse(accountCommitResponseLog);
                    commitResponseList.add(accountCommitResponse);
                    return true;
                }
            }
        }
        AccountCommitStatus commitStatus = new AccountCommitStatus(depositCommit.getCUID(), "failed");
        logger.saveCommitStatus(commitStatus);
        commitStatusList.add(commitStatus);
        AccountCommitLog accountCommitLog = new AccountCommitLog(depositCommit,null);
        logger.saveCommit(accountCommitLog);
        AccountCommitResponse accountCommitResponse = new DepositCommitResponse(depositCommit, false);
        AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountCommitResponse);
        logger.saveCommitResponse(accountCommitResponseLog);
        commitResponseList.add(accountCommitResponse);
        return false;
    }

    public boolean accountTransactionCommitConsumer(AccountTransactionCommit accountTransactionCommit) {
        AccountCommitLog accountCommitLog = new AccountCommitLog(accountTransactionCommit, accountTransactionCommit.getAccountId());
        logger.saveCommit(accountCommitLog);

        List<String> transactionList = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        Account account = accountDAL.loadAccount(accountTransactionCommit.getAccountId());
        String balance = null;

        if (account != null) {
            balance = account.getBalance();
            AccountCommitLog commit;
            int numberOfRecordsOfTransaction = accountTransactionCommit.getNumberOfTransactions();
            String commitId = account.getLastCommitId();
            while (numberOfRecordsOfTransaction > 0) {
                commit = logger.findCommitById(commitId);
                if (commit.getCommitType() == WithdrawCommit.class.getName() || commit.getCommitType() == DepositCommit.class.getName()) {
                    transactionList.add(commit.getCommitType());
                    dateList.add(commit.getTime());
                    numberOfRecordsOfTransaction--;
                    commitId = commit.getPreviousCommitId();
                }
            }
        }

        if (transactionList.size() > 0) {
            AccountCommitStatus commitStatus = new AccountCommitStatus(accountTransactionCommit.getCUID(), "done");
            logger.saveCommitStatus(commitStatus);
            commitStatusList.add(commitStatus);
            AccountCommitResponse accountCommitResponse = new AccountTransactionsCommitResponse(accountTransactionCommit, balance, transactionList, dateList);
            AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountCommitResponse);
            logger.saveCommitResponse(accountCommitResponseLog);
            commitResponseList.add(accountCommitResponse);
            return true;
        }
        AccountCommitStatus commitStatus = new AccountCommitStatus(accountTransactionCommit.getCUID(), "failed");
        logger.saveCommitStatus(commitStatus);
        commitStatusList.add(commitStatus);
        AccountCommitResponse accountCommitResponse = new AccountTransactionsCommitResponse(accountTransactionCommit, balance, null, null);
        AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountCommitResponse);
        logger.saveCommitResponse(accountCommitResponseLog);
        commitResponseList.add(accountCommitResponse);
        return false;
    }

    public boolean createAccountCommitConsumer(CreateAccountCommit commit) {
        for (int i = 0; i < commitStatusList.size(); i++) {
            if (commitStatusList.get(i).getCommitId().equals(commit.getCUID())) {
                return true;
            }
        }
        String time = Long.toString(System.currentTimeMillis());
        String accountId = time + commit.getUserId();
        Account newAccount = new Account(accountId, commit.getUserId(), commit.getCUID());

        accounts.add(newAccount);

        this.accountDAL.saveAccount(newAccount);

        AccountCommitStatus status = new AccountCommitStatus(commit.getCUID(), "Done");
        this.commitStatusList.add(status);

        CreateAccountCommitResponse commitResponse = new CreateAccountCommitResponse(commit, accountId);
        this.commitResponseList.add(commitResponse);

        AccountCommitLog accountCommitLog = new AccountCommitLog(commit, null);
        AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(commitResponse);
        logger.saveCommit(accountCommitLog);
        logger.saveCommitResponse(accountCommitResponseLog);
        logger.saveCommitStatus(status);

        return true;
    }

    public boolean accountHistoryCommitConsumer(AccountHistoryCommit accountHistoryCommit) {
        for (AccountCommitStatus commitstus : commitStatusList) {
            if (commitstus.getCommitId().equals(accountHistoryCommit.getCUID())) {
                return true;
            }
        }
        String previousCommitId = null;
        List<String> event = new ArrayList<>();
        List<String> time = new ArrayList<>();
        AccountCommitLog commit;
        for (Account account : accounts) {
            if (account.getAccountId().equals(accountHistoryCommit.getAccountId())) {
                previousCommitId = account.getLastCommitId();
                AccountCommitLog accountCommitLog = new AccountCommitLog(accountHistoryCommit, previousCommitId);
                logger.saveCommit(accountCommitLog);
                int numberEvent = accountHistoryCommit.getNumberOfHistoryEvent();
                while (numberEvent > 0) {
                    commit = logger.findCommitById(previousCommitId);
                    if (commit == null) {
                        break;
                    }
                    if (!commit.getCommitType().equals("Withdraw") && !commit.getCommitType().equals("Deposit")) {
                        event.add(commit.getCommitType());
                        time.add(commit.getTime());
                        numberEvent--;
                    }
                    previousCommitId = commit.getPreviousCommitId();
                }
            }
        }
        if (event.size() > 0) {
            AccountCommitStatus commitStatus = new AccountCommitStatus(accountHistoryCommit.getCUID(), "done");
            logger.saveCommitStatus(commitStatus);
            commitStatusList.add(commitStatus);
            AccountHistoryCommitResponse accountHistoryCommitResponse = new AccountHistoryCommitResponse(accountHistoryCommit, event, time);
            AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountHistoryCommitResponse);
            logger.saveCommitResponse(accountCommitResponseLog);
            commitResponseList.add(accountHistoryCommitResponse);
            return true;
        }
        AccountCommitLog accountCommitLog = new AccountCommitLog(accountHistoryCommit, previousCommitId);
        logger.saveCommit(accountCommitLog);
        AccountCommitStatus commitStatus = new AccountCommitStatus(accountHistoryCommit.getCUID(), "failed");
        logger.saveCommitStatus(commitStatus);
        commitStatusList.add(commitStatus);
        AccountHistoryCommitResponse accountHistoryCommitResponse = new AccountHistoryCommitResponse(accountHistoryCommit, null, null);
        AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountHistoryCommitResponse);
        logger.saveCommitResponse(accountCommitResponseLog);
        commitResponseList.add(accountHistoryCommitResponse);
        return false;
    }

    public List<AccountCommit> loadAccountCommitsFromXML() {
        List<AccountCommit> accountCommitList = new ArrayList<>();
        try {
            File accountXML = new File("AccountCommits.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(accountXML);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("AccountCommits");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element accountCommit = (Element) nNode;

                    Node creatAccountCommit = accountCommit.getElementsByTagName("CreatAccountCommit").item(0);
                    NodeList list1 = creatAccountCommit.getChildNodes();
                    String userId = "";
                    String commitId = "";
                    for (int j = 0; i < list1.getLength(); j++) {
                        Node child = list1.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listCreatAccountCommit = (Element) nNode;
                            userId = listCreatAccountCommit.getElementsByTagName("userId").item(0).getTextContent();
                            commitId = listCreatAccountCommit.getElementsByTagName("commitId").item(0).getTextContent();
                        }
                    }
                    CreateAccountCommit creatAccountCommi = new CreateAccountCommit(userId);
                    creatAccountCommi.setCUID(commitId);
                    accountCommitList.add(creatAccountCommi);

                    Node deactiveAccountCommit = accountCommit.getElementsByTagName("DeactivateAccountCommit").item(0);
                    NodeList list2 = deactiveAccountCommit.getChildNodes();
                    String accountId = "";
                    for (int j = 0; j < list2.getLength(); j++) {
                        Node child = list2.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listDeactiveAccountCommit = (Element) nNode;
                            userId = listDeactiveAccountCommit.getElementsByTagName("userId").item(0).getTextContent();
                            commitId = listDeactiveAccountCommit.getElementsByTagName("commitId").item(0).getTextContent();
                            commitId = listDeactiveAccountCommit.getElementsByTagName("accountId").item(0).getTextContent();
                        }
                    }
                    DeactivateAccountCommit deactivateAccountCommi = new DeactivateAccountCommit(accountId);
                    deactivateAccountCommi.setCUID(commitId);
                    accountCommitList.add(deactivateAccountCommi);
                    Node reactiveAccountCommit = accountCommit.getElementsByTagName("ReactivateAccountCommit").item(0);
                    NodeList list3 = reactiveAccountCommit.getChildNodes();
                    for (int j = 0; j < list3.getLength(); j++) {
                        Node child = list3.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listReactiveAccountCommit = (Element) nNode;
                            accountId = listReactiveAccountCommit.getElementsByTagName("accountId").item(0).getTextContent();
                            commitId = listReactiveAccountCommit.getElementsByTagName("commitId").item(0).getTextContent();
                        }
                    }
                    ReactivateAccountCommit reactivateAccountCommi = new ReactivateAccountCommit();
                    reactivateAccountCommi.setAccountId(accountId);
                    reactivateAccountCommi.setCUID(commitId);
                    accountCommitList.add(reactivateAccountCommi);

                    Node withdrawCommit = accountCommit.getElementsByTagName("WithdrawCommit").item(0);
                    NodeList list4 = withdrawCommit.getChildNodes();
                    String amount = "";
                    for (int j = 0; j < list4.getLength(); j++) {
                        Node child = list4.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listWithdrawCommit = (Element) nNode;
                            accountId = listWithdrawCommit.getElementsByTagName("accountId").item(0).getTextContent();
                            amount = listWithdrawCommit.getElementsByTagName("amount").item(0).getTextContent();
                            commitId = listWithdrawCommit.getElementsByTagName("commitId").item(0).getTextContent();
                        }
                    }
                    WithdrawCommit withdrawCommi = new WithdrawCommit();
                    withdrawCommi.setAccountId(accountId);
                    withdrawCommi.setAmount(amount);
                    withdrawCommi.setCUID(commitId);
                    accountCommitList.add(withdrawCommi);

                    Node depositCommit = accountCommit.getElementsByTagName("DepositCommit").item(0);
                    NodeList list5 = depositCommit.getChildNodes();
                    for (int j = 0; j < list5.getLength(); j++) {
                        Node child = list5.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listDepositCommit = (Element) nNode;
                            accountId = listDepositCommit.getElementsByTagName("accountId").item(0).getTextContent();
                            amount = listDepositCommit.getElementsByTagName("amount").item(0).getTextContent();
                            commitId = listDepositCommit.getElementsByTagName("commitId").item(0).getTextContent();
                        }
                    }
                    DepositCommit depositCommi = new DepositCommit(accountId,amount);
                    depositCommi.setCUID(commitId);
                    accountCommitList.add(depositCommi);

                    Node accountTransactionCommit = accountCommit.getElementsByTagName("AccountTransactionCommit").item(0);
                    NodeList list6 = accountTransactionCommit.getChildNodes();
                    String numberOfTransactions = "";
                    for (int j = 0; j < list6.getLength(); j++) {
                        Node child = list6.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listAccountTransactionCommit = (Element) nNode;
                            accountId = listAccountTransactionCommit.getElementsByTagName("accountId").item(0).getTextContent();
                            numberOfTransactions = listAccountTransactionCommit.getElementsByTagName("numberOfTransactions").item(0).getTextContent();
                            commitId = listAccountTransactionCommit.getElementsByTagName("commitId").item(0).getTextContent();
                        }
                    }
                    AccountTransactionCommit accountTransactionCommi = new AccountTransactionCommit();
                    accountTransactionCommi.setAccountId(accountId);
                    accountTransactionCommi.setNumberOfTransactions(Integer.parseInt(numberOfTransactions));
                    accountTransactionCommi.setCUID(commitId);
                    accountCommitList.add(accountTransactionCommi);

                    Node blackAccountCommit = accountCommit.getElementsByTagName("BlockAccountCommit").item(0);
                    NodeList list7 = blackAccountCommit.getChildNodes();
                    for (int j = 0; j < list7.getLength(); j++) {
                        Node child = list7.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listBlackAccountCommit = (Element) nNode;
                            accountId = listBlackAccountCommit.getElementsByTagName("accountId").item(0).getTextContent();
                            commitId = listBlackAccountCommit.getElementsByTagName("commitId").item(0).getTextContent();
                        }
                    }
                    BlockAccountCommit blackAccountCommi = new BlockAccountCommit();
                    blackAccountCommi.setAccountId(accountId);
                    blackAccountCommi.setCUID(commitId);
                    accountCommitList.add(blackAccountCommi);

                    Node accoutHistoryCommit = accountCommit.getElementsByTagName("AccountHistoryCommit").item(0);
                    NodeList list8 = accoutHistoryCommit.getChildNodes();
                    String numberOfHistoryEvents = "";
                    for (int j = 0; j < list8.getLength(); j++) {
                        Node child = list8.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listAccoutHistoryCommit = (Element) nNode;
                            accountId = listAccoutHistoryCommit.getElementsByTagName("accountId").item(0).getTextContent();
                            numberOfHistoryEvents = listAccoutHistoryCommit.getElementsByTagName("numberOfHistoryEvents").item(0).getTextContent();
                            commitId = listAccoutHistoryCommit.getElementsByTagName("commitId").item(0).getTextContent();
                        }
                    }
                    AccountHistoryCommit accoutHistoryCommi = new AccountHistoryCommit();
                    accoutHistoryCommi.setAccountId(accountId);
                    accoutHistoryCommi.setNumberOfHistoryEvent(Integer.parseInt(numberOfHistoryEvents));
                    accoutHistoryCommi.setCUID(commitId);
                    accountCommitList.add(accoutHistoryCommi);

                    Node unblockAccountCommit = accountCommit.getElementsByTagName("UnblockAccountCommit").item(0);
                    NodeList list9 = unblockAccountCommit.getChildNodes();
                    for (int j = 0; j < list9.getLength(); j++) {
                        Node child = list9.item(j);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element listUnblockAccountCommit = (Element) nNode;
                            accountId = listUnblockAccountCommit.getElementsByTagName("accountId").item(0).getTextContent();
                            commitId = listUnblockAccountCommit.getElementsByTagName("commitId").item(0).getTextContent();
                        }
                    }
                    UnblockAccountCommit unblockAccountCommi = new UnblockAccountCommit();
                    unblockAccountCommi.setAccountId(accountId);
                    unblockAccountCommi.setCUID(commitId);
                    accountCommitList.add(unblockAccountCommi);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountCommitList;
    }

}
